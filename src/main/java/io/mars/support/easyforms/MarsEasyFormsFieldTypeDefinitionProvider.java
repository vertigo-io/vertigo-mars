package io.mars.support.easyforms;

import java.util.Map;

import io.mars.basemanagement.domain.Business;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleEnumDefinitionProvider;
import io.vertigo.core.util.StringUtil;
import io.vertigo.easyforms.runner.model.definitions.EasyFormsFieldTypeDefinition;
import io.vertigo.easyforms.runner.model.definitions.EasyFormsUiComponentDefinition;
import io.vertigo.easyforms.runner.pack.EasyFormsSmartTypes;
import io.vertigo.easyforms.runner.pack.provider.FieldValidatorTypeDefinitionProvider.FieldValidatorEnum;
import io.vertigo.easyforms.runner.pack.provider.UiComponentDefinitionProvider.UiComponentEnum;
import io.vertigo.easyforms.runner.pack.provider.fieldtype.SimpleFieldType;
import io.vertigo.easyforms.runner.pack.provider.uicomponent.SelectUiComponent;
import io.vertigo.easyforms.runner.suppliers.IEasyFormsFieldTypeDefinitionSupplier;
import io.vertigo.easyforms.runner.suppliers.IEasyFormsUiComponentDefinitionSupplier;

public class MarsEasyFormsFieldTypeDefinitionProvider implements SimpleEnumDefinitionProvider<EasyFormsFieldTypeDefinition> {

	public enum FieldTypeEnum implements EnumDefinition<EasyFormsFieldTypeDefinition, FieldValidatorEnum> {

		BUSINESS_LIST(new BusinessFieldType()),
		;
		// ---

		private final String definitionName;
		private final IEasyFormsFieldTypeDefinitionSupplier typeSupplier;

		private FieldTypeEnum(final EasyFormsSmartTypes smartType, final EnumDefinition<EasyFormsUiComponentDefinition, ?> uiComponent) {
			this(new SimpleFieldType(smartType, uiComponent));
		}

		private FieldTypeEnum(final IEasyFormsFieldTypeDefinitionSupplier typeSupplier) {
			definitionName = EasyFormsFieldTypeDefinition.PREFIX + StringUtil.constToUpperCamelCase(name());
			this.typeSupplier = typeSupplier;
		}

		@Override
		public EasyFormsFieldTypeDefinition buildDefinition(final DefinitionSpace definitionSpace) {
			return typeSupplier.get(definitionName);
		}

		@Override
		public EasyFormsFieldTypeDefinition get() {
			return EasyFormsFieldTypeDefinition.resolve(definitionName);
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

	private static class BusinessFieldType implements IEasyFormsFieldTypeDefinitionSupplier {
		@Override
		public EasyFormsSmartTypes getSmartType() {
			return EasyFormsSmartTypes.EfId;
		}

		@Override
		public EnumDefinition<EasyFormsUiComponentDefinition, ?> getUiComponent() {
			return UiComponentEnum.SELECT;
		}

		@Override
		public Map<String, Object> getUiParams() {
			return Map.of(
					IEasyFormsUiComponentDefinitionSupplier.LIST_SUPPLIER, IEasyFormsUiComponentDefinitionSupplier.getMdlSupplier(Business.class),
					SelectUiComponent.SEARCHABLE, true);
		}

		@Override
		public Object getDefaultValue() {
			return 1003; // Communication
		}
	}
}
