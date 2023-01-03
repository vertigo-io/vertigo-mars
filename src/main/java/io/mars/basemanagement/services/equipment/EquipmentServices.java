package io.mars.basemanagement.services.equipment;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.mars.authorization.SecuredEntities;
import io.mars.basemanagement.BasemanagementPAO;
import io.mars.basemanagement.dao.EquipmentDAO;
import io.mars.basemanagement.domain.Equipment;
import io.mars.basemanagement.domain.EquipmentIndex;
import io.mars.basemanagement.domain.EquipmentMaintenanceOverview;
import io.mars.basemanagement.domain.EquipmentOverview;
import io.mars.basemanagement.domain.GeoSearchEquipmentCriteria;
import io.mars.basemanagement.search.EquipmentIndexSearchClient;
import io.mars.support.smarttypes.GeoPoint;
import io.vertigo.account.account.Account;
import io.vertigo.account.authentication.AuthenticationManager;
import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.AuthorizationUtil;
import io.vertigo.account.authorization.VSecurityException;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Component;
import io.vertigo.datafactory.collections.ListFilter;
import io.vertigo.datafactory.collections.definitions.FacetDefinition;
import io.vertigo.datafactory.collections.model.FacetedQueryResult;
import io.vertigo.datafactory.collections.model.SelectedFacetValues;
import io.vertigo.datafactory.search.model.SearchQuery;
import io.vertigo.datafactory.search.model.SearchQueryBuilder;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.social.comment.Comment;
import io.vertigo.social.comment.CommentManager;

@Transactional
public class EquipmentServices implements Component {

	@Inject
	private BasemanagementPAO basemanagementPAO;
	@Inject
	private EquipmentDAO equipmentDAO;
	@Inject
	private EquipmentIndexSearchClient equipmentIndexSearchClient;
	@Inject
	private CommentManager commentManager;
	@Inject
	private AuthenticationManager authenticationManager;
	@Inject
	private AuthorizationManager authorizationManager;

	public Equipment get(final Long equipmentId) {
		final Equipment equipment = equipmentDAO.get(equipmentId);
		AuthorizationUtil.assertOperations(equipment, SecuredEntities.EquipmentOperations.read);
		//-----
		equipment.equipmentType().load();
		equipment.business().load();
		return equipment;
	}

	public void save(final Equipment equipment) {
		final Equipment originalEquipment = AuthorizationUtil.assertOperationsOnOriginalEntity(equipment, SecuredEntities.EquipmentOperations.write);
		if (equipment.getBaseId() != null
				&& (originalEquipment.getBaseId() != equipment.getBaseId()
						|| equipment.getEquipmentId() == null)) {
			//If equipment is new or is affected to a new base, we check addEquip authorization
			equipment.base().load();
			AuthorizationUtil.assertOperations(equipment.base().get(), SecuredEntities.BaseOperations.addEqui);
		}
		//-----
		equipmentDAO.save(equipment);
	}

	public DtList<Equipment> getEquipments(final DtListState dtListState) {
		final Criteria<Equipment> securityFilter = authorizationManager.getCriteriaSecurity(Equipment.class, SecuredEntities.EquipmentOperations.read);
		return equipmentDAO.findAll(securityFilter, dtListState);
	}

	public DtList<EquipmentIndex> getEquipmentIndex(final List<Long> equipmentIds) {
		//use by searchLoader : no security
		return basemanagementPAO.loadEquipmentIndex(equipmentIds);
	}

	public FacetedQueryResult<EquipmentIndex, SearchQuery> searchEquipments(final String criteria, final SelectedFacetValues selectedFacetValues, final DtListState dtListState) {
		final ListFilter securityListFilter = ListFilter.of(authorizationManager.getSearchSecurity(Equipment.class, SecuredEntities.EquipmentOperations.read));
		final SearchQuery searchQuery = equipmentIndexSearchClient.createSearchQueryBuilderEquipment(criteria, selectedFacetValues)
				.withSecurityFilter(securityListFilter)
				.build();
		return equipmentIndexSearchClient.loadList(searchQuery, dtListState);
	}

	public FacetedQueryResult<EquipmentIndex, SearchQuery> searchGeoEquipments(final GeoSearchEquipmentCriteria criteria, final SelectedFacetValues selectedFacetValues, final DtListState dtListState) {
		final ListFilter securityListFilter = ListFilter.of(authorizationManager.getSearchSecurity(Equipment.class, SecuredEntities.EquipmentOperations.read));
		final SearchQuery searchQuery;
		if (criteria.getGeoLocation() != null) {
			searchQuery = equipmentIndexSearchClient.createSearchQueryBuilderEquipmentGeoDistance(criteria, selectedFacetValues)
					.withSecurityFilter(securityListFilter)
					.build();
		} else {
			searchQuery = equipmentIndexSearchClient.createSearchQueryBuilderEquipmentGeo(criteria, selectedFacetValues)
					.withSecurityFilter(securityListFilter)
					.build();
		}
		return equipmentIndexSearchClient.loadList(searchQuery, dtListState);
	}

	private static final double[] AREA_PER_GEOHASH_PRECISION = { 2025.0, 63.28125, 1.9775390625, 0.061798095703125, 0.0019311904907226562, 6.034970283508301e-05, 1.885928213596344e-06, 5.893525667488575e-08 };

	private static long obtainBestPrecision(final GeoPoint geoUpperLeft, final GeoPoint geoLowerRight) {
		if (geoUpperLeft == null || geoLowerRight == null) {
			return 2;
		}
		final double y1 = geoUpperLeft.getLat();
		final double x1 = geoUpperLeft.getLon();
		final double y2 = geoLowerRight.getLat();
		final double x2 = geoLowerRight.getLon();
		final double boundingBoxArea = Math.min(Math.abs(x1 - x2), Math.abs(Math.min(x1, x2) + 180 - Math.max(x1, x2))) * Math.abs(y1 - y2);
		for (int precision = 0; precision < AREA_PER_GEOHASH_PRECISION.length; precision++) {
			final double nbBucket = boundingBoxArea / AREA_PER_GEOHASH_PRECISION[precision];
			if (nbBucket >= 250) { //minimum 250 buckets sinon trop calé sur la grille
				if (nbBucket >= 5000) {
					return precision; //si trop de bucket on reste au niveau inférieur
				}
				return precision + 1;
			}
		}
		return AREA_PER_GEOHASH_PRECISION.length;
	}

	public FacetedQueryResult<EquipmentIndex, SearchQuery> searchGeoClusterEquipments(final GeoSearchEquipmentCriteria criteria, final SelectedFacetValues selectedFacetValues, final DtListState dtListState) {
		final ListFilter securityListFilter = ListFilter.of(authorizationManager.getSearchSecurity(Equipment.class, SecuredEntities.EquipmentOperations.read));
		final SearchQueryBuilder searchQueryBuilder;
		criteria.setGeoPrecision(obtainBestPrecision(criteria.getGeoUpperLeft(), criteria.getGeoLowerRight()));
		if (criteria.getGeoLocation() != null) {
			searchQueryBuilder = equipmentIndexSearchClient.createSearchQueryBuilderEquipmentGeoDistance(criteria, selectedFacetValues);
		} else {
			searchQueryBuilder = equipmentIndexSearchClient.createSearchQueryBuilderEquipmentGeo(criteria, selectedFacetValues);
		}
		final FacetDefinition clusteringFacetDefinition = Node.getNode().getDefinitionSpace().resolve("FctEquipmentGeoHash", FacetDefinition.class);
		searchQueryBuilder.withFacetClustering(clusteringFacetDefinition)
				.withSecurityFilter(securityListFilter);

		return equipmentIndexSearchClient.loadList(searchQueryBuilder.build(), dtListState);
	}

	public DtList<EquipmentOverview> getEquipmentOverviewByBaseId(final Long baseId) {
		Assertion.check().isNotNull(baseId);
		//---
		return basemanagementPAO.getEquipmentsOverview(baseId,
				AuthorizationUtil.authorizationCriteria(Equipment.class, SecuredEntities.EquipmentOperations.read));
	}

	public DtList<Equipment> getLastPurchasedEquipmentsByBase(final Long baseId) {
		Assertion.check().isNotNull(baseId);
		//---
		return equipmentDAO.getLastPurchasedEquipmentsByBaseId(baseId,
				AuthorizationUtil.authorizationCriteria(Equipment.class, SecuredEntities.EquipmentOperations.read));
	}

	public DtList<Equipment> getEquipmentByBase(final String baseCode) {
		Assertion.check().isNotNull(baseCode);
		//---
		return equipmentDAO.getEquipmentsByBaseCode(baseCode,
				AuthorizationUtil.authorizationCriteria(Equipment.class, SecuredEntities.EquipmentOperations.read));
	}

	public EquipmentMaintenanceOverview getMaintenanceOverviewByEquipment(final Long equipmentId) {
		Assertion.check().isNotNull(equipmentId);
		//---
		return basemanagementPAO.getEquipmentMaintenanceOverview(equipmentId);
	}

	public void addCommentToEquipment(final String commentString, final Long equipmentId) {
		Assertion.check().isNotNull(equipmentId);
		//

		final Account currentAccount = authenticationManager.getLoggedAccount().orElseThrow(() -> new VSecurityException(LocaleMessageText.of("No user currently logged in")));
		final UID<Account> currentAccountUID = currentAccount.getUID();

		final Comment comment = Comment.builder()
				.withAuthor(currentAccountUID)
				.withMsg(commentString)
				.build();

		commentManager.publish(currentAccountUID, comment, UID.of(Equipment.class, equipmentId));
	}

	public List<String> getComments(final Long equipmentId) {
		return commentManager.getComments(UID.of(Equipment.class, equipmentId))
				.stream()
				.map(Comment::msg)
				.collect(Collectors.toList());
	}
}
