package io.mars.support.boot;

import java.io.Serializable;
import java.util.Map;

import io.mars.basemanagement.domain.Business;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleEnumDefinitionProvider;
import io.vertigo.core.util.StringUtil;
import io.vertigo.easyforms.easyformsrunner.model.EasyFormsFieldType;
import io.vertigo.easyforms.easyformsrunner.model.EasyFormsUiComponent;
import io.vertigo.easyforms.easyformsrunner.model.IEasyFormsFieldTypeSupplier;
import io.vertigo.easyforms.easyformsrunner.model.IEasyFormsUiComponentSupplier;
import io.vertigo.easyforms.impl.easyformsrunner.library.EasyFormsSmartTypes;
import io.vertigo.easyforms.impl.easyformsrunner.library.provider.FieldTypeDefinitionProvider.SimpleFieldType;
import io.vertigo.easyforms.impl.easyformsrunner.library.provider.FieldValidatorDefinitionProvider.FieldValidatorEnum;
import io.vertigo.easyforms.impl.easyformsrunner.library.provider.UiComponentDefinitionProvider.UiComponentEnum;

public class MarsEasyFormsFieldTypeDefinitionProvider implements SimpleEnumDefinitionProvider<EasyFormsFieldType> {

	public enum FieldTypeEnum implements EnumDefinition<EasyFormsFieldType, FieldValidatorEnum> {

		BUSINESS_LIST(new BusinessFieldType()),
		;
		// ---

		private final String definitionName;
		private final IEasyFormsFieldTypeSupplier typeSupplier;

		private FieldTypeEnum(final EasyFormsSmartTypes smartType, final EnumDefinition<EasyFormsUiComponent, ?> uiComponent) {
			this(new SimpleFieldType(smartType, uiComponent));
		}

		private FieldTypeEnum(final IEasyFormsFieldTypeSupplier typeSupplier) {
			definitionName = EasyFormsFieldType.PREFIX + StringUtil.constToUpperCamelCase(name());
			this.typeSupplier = typeSupplier;
		}

		@Override
		public EasyFormsFieldType buildDefinition(final DefinitionSpace definitionSpace) {
			return typeSupplier.get(definitionName);
		}

		@Override
		public EasyFormsFieldType get() {
			return EasyFormsFieldType.resolve(definitionName);
		}

		@Override
		public String getDefinitionName() {
			return definitionName;
		}

	}

	@Override
	public Class<FieldTypeEnum> getEnumClass() {
		return FieldTypeEnum.class;
	}

	private static class BusinessFieldType implements IEasyFormsFieldTypeSupplier {
		@Override
		public EasyFormsSmartTypes getSmartType() {
			return EasyFormsSmartTypes.EfId;
		}

		@Override
		public EnumDefinition<EasyFormsUiComponent, ?> getUiComponent() {
			return UiComponentEnum.RADIO;
		}

		@Override
		public Map<String, Serializable> getUiParams() {
			return Map.of(IEasyFormsUiComponentSupplier.LIST_SUPPLIER, IEasyFormsUiComponentSupplier.getMdlSupplier(Business.class));
			//			return Map.of(IEasyFormsUiComponentSupplier.LIST_SUPPLIER, IEasyFormsUiComponentSupplier.getCtxSupplier("persons"));
		}

		@Override
		public Object getDefaultValue() {
			return 1003; // Communication
		}
	}
}
