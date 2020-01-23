package io.mars.support;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import io.vertigo.core.lang.DataStream;
import io.vertigo.dynamo.ngdomain.annotations.Constraint;
import io.vertigo.dynamo.ngdomain.annotations.Formatter;
import io.vertigo.dynamo.ngdomain.annotations.FormatterDefault;
import io.vertigo.dynamo.ngdomain.annotations.SmartTypeDefinition;
import io.vertigo.dynamo.ngdomain.annotations.SmartTypeProperty;
import io.vertigo.dynamox.domain.constraint.ConstraintNumberMaximum;
import io.vertigo.dynamox.domain.constraint.ConstraintNumberMinimum;
import io.vertigo.dynamox.domain.constraint.ConstraintRegex;
import io.vertigo.dynamox.domain.constraint.ConstraintStringLength;
import io.vertigo.dynamox.domain.formatter.FormatterDate;
import io.vertigo.dynamox.domain.formatter.FormatterId;

public enum MarsSmartTypes {

	@SmartTypeDefinition(Long.class)
	@Formatter(clazz = FormatterId.class, arg = "")
	@SmartTypeProperty(property = "storeType", value = "NUMERIC")
	Id,

	@SmartTypeDefinition(String.class)
	@FormatterDefault
	@SmartTypeProperty(property = "storeType", value = "TEXT")
	MultipleIds,

	@SmartTypeDefinition(Instant.class)
	@Formatter(clazz = FormatterDate.class, arg = "dd/MM/yyyy ' ' HH'h'mm")
	@SmartTypeProperty(property = "storeType", value = "TIMESTAMP")
	Instant,

	@SmartTypeDefinition(LocalDate.class)
	@Formatter(clazz = FormatterDate.class, arg = "dd/MM/yyyy")
	@SmartTypeProperty(property = "storeType", value = "DATE")
	Localdate,

	@SmartTypeDefinition(BigDecimal.class)
	@FormatterDefault
	@SmartTypeProperty(property = "storeType", value = "NUMERIC(12,2)")
	@SmartTypeProperty(property = "unit", value = "$")
	Currency,

	@SmartTypeDefinition(Integer.class)
	@FormatterDefault
	@Constraint(clazz = ConstraintNumberMinimum.class, arg = "0", msg = "")
	@Constraint(clazz = ConstraintNumberMaximum.class, arg = "100", msg = "")
	@SmartTypeProperty(property = "storeType", value = "NUMERIC")
	Health,

	@SmartTypeDefinition(String.class)
	@FormatterDefault
	@Constraint(clazz = ConstraintStringLength.class, arg = "100", msg = "")
	@SmartTypeProperty(property = "storeType", value = "VARCHAR(100)")
	@SmartTypeProperty(property = "indexType", value = "text_fr:sortable")
	Label,

	@SmartTypeDefinition(String.class)
	@FormatterDefault
	@Constraint(clazz = ConstraintStringLength.class, arg = "350", msg = "")
	@SmartTypeProperty(property = "storeType", value = "VARCHAR(350)")
	@SmartTypeProperty(property = "indexType", value = "text_fr")
	Description,

	@SmartTypeDefinition(String.class)
	@FormatterDefault
	@Constraint(clazz = ConstraintRegex.class, arg = "^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*(\\.[a-zA-Z0-9-]{2,3})+$", msg = "L'email n'est pas valide")
	@Constraint(clazz = ConstraintStringLength.class, arg = "150", msg = "")
	@SmartTypeProperty(property = "storeType", value = "VARCHAR(150)")
	Email,

	@SmartTypeDefinition(String.class)
	@FormatterDefault
	@SmartTypeProperty(property = "storeType", value = "TEXT")
	Url,

	@SmartTypeDefinition(String.class)
	@FormatterDefault
	@Constraint(clazz = ConstraintStringLength.class, arg = "100", msg = "")
	@SmartTypeProperty(property = "storeType", value = "VARCHAR(100)")
	@SmartTypeProperty(property = "indexType", value = "code:keyword")
	Code,

	@SmartTypeDefinition(String.class)
	@FormatterDefault
	@Constraint(clazz = ConstraintStringLength.class, arg = "32", msg = "")
	@SmartTypeProperty(property = "storeType", value = "VARCHAR(32)")
	Password,

	@SmartTypeDefinition(String.class)
	@FormatterDefault
	@SmartTypeProperty(property = "storeType", value = "TEXT")
	Text,

	@SmartTypeDefinition(String.class)
	@FormatterDefault
	@SmartTypeProperty(property = "storeType", value = "VARCHAR(500)")
	FilePath,

	@SmartTypeDefinition(DataStream.class)
	@FormatterDefault
	@SmartTypeProperty(property = "storeType", value = "bytea")
	FileData,

	@SmartTypeDefinition(String.class)
	@FormatterDefault
	@SmartTypeProperty(property = "storeType", value = "TEXT")
	@SmartTypeProperty(property = "indexType", value = "multiple_code:facetable")
	Tags,

	@SmartTypeDefinition(Boolean.class)
	@FormatterDefault
	@SmartTypeProperty(property = "storeType", value = "bool")
	YesNo,

	@SmartTypeDefinition(Long.class)
	@FormatterDefault
	@SmartTypeProperty(property = "storeType", value = "NUMERIC")
	Size,

	@SmartTypeDefinition(Long.class)
	@FormatterDefault
	@SmartTypeProperty(property = "storeType", value = "NUMERIC")
	Count;

}
