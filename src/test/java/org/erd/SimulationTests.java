package org.erd;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.inject.Inject;
import jakarta.validation.Validator;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class SimulationTests {
	@Inject
	Validator validator;

	@Test
	void basicCheck() {
		var simulation = Simulation.builder()
			.name("Elias")
			.cpf("123456")
			.email("elias@elias.com")
			.amount(new BigDecimal(1000))
			.installments(48)
			.insurance(false)
			.build();

		assertThat(simulation.getAmount())
			.usingComparator(BigDecimal::compareTo)
			.isBetween(new BigDecimal(1000), new BigDecimal(40000));

		assertThat(simulation.getInstallments()).isBetween(2, 48);
	}

	@Test
	void simulationErrorAssertion() {
		var simulation = Simulation.builder()
			.name("John")
			.cpf("9582728395")
			.email("john@gmail.com")
			.amount(new BigDecimal("1.500"))
			.installments(5)
			.insurance(false)
			.build();

		SimulationAssert.assertThat(simulation, validator)
			.hasValidInstallments()
			.hasValidAmount()
			.hasValidAmountUsingValidation();
	}

	@Test
	void simulationValidationAssertion() {
		var simulation = Simulation.builder()
			.name("John")
			.cpf("9582728395")
			.email("john@gmail.com")
			.amount(new BigDecimal("1.500"))
			.installments(5)
			.insurance(false)
			.build();

		SimulationAssert.assertThat(simulation, this.validator)
			.hasValidInstallments()
			.hasValidAmount()
			.hasNameEqualsTo("John");
	}

	@ParameterizedTest
	@CsvSource(delimiter = '|', textBlock = """
		15  | 5
		10  | 48
		""")
	void simulationSucceds(String amount, int installments) {
		var simulation = Simulation.builder()
			.name("John")
			.cpf("9582728395")
			.email("john@gmail.com")
			.amount(new BigDecimal(amount))
			.installments(installments)
			.insurance(false)
			.build();

		SimulationAssert.assertThat(simulation, validator)
			.isValid();
	}

	@ParameterizedTest
	@CsvSource(delimiter = '|', textBlock = """
		500  | 5
		1000 | 49
		""")
	void simulationFails(String amount, int installments) {
		var simulation = Simulation.builder()
			.name("John")
			.cpf("9582728395")
			.email("john@gmail.com")
			.amount(new BigDecimal(amount))
			.installments(installments)
			.insurance(false)
			.build();

		SimulationAssert.assertThat(simulation, validator)
			.isNotValid();
	}

	public static class SimulationAssert extends AbstractAssert<SimulationAssert, Simulation> {
		private final Validator validator;

		protected SimulationAssert(Simulation simulation, Validator validator) {
			super(simulation, SimulationAssert.class);
			this.validator = validator;
		}

		public static SimulationAssert assertThat(Simulation simulation, Validator validator) {
			return new SimulationAssert(simulation, validator);
		}

		public SimulationAssert hasValidInstallments() {
			isNotNull();

			if (actual.getInstallments() < 2 || actual.getInstallments() > 48) {
				failWithMessage("Installments must be must be equal or greater than 2 and equal or less than 48");
			}

			return this;
		}

		public SimulationAssert hasValidAmount() {
			isNotNull();

			var minimum = new BigDecimal("1.000");
			var maximum = new BigDecimal("40.000");

			if (actual.getAmount().compareTo(minimum) < 0 || actual.getAmount().compareTo(maximum) > 0) {
				failWithMessage("Amount must be equal or greater than $ 1.000 or equal or less than than $ 40.000");
			}

			return this;
		}

		public SimulationAssert hasValidAmountUsingValidation() {
			isNotNull();

			Assertions.assertThat(this.validator.validate(actual))
				.isNullOrEmpty();

			return this;
		}

		public SimulationAssert hasNameEqualsTo(String name) {
			isNotNull();

			if (!Objects.equals(actual.getName(), name)) {
				failWithMessage("Expect the Simulation to have the name equals to %s", name);
			}

			return this;
		}

		public SimulationAssert isValid() {
			isNotNull();

			Assertions.assertThat(this.validator.validate(actual))
				.isNullOrEmpty();

			return this;
		}

		public SimulationAssert isNotValid() {
			isNotNull();

			Assertions.assertThat(this.validator.validate(actual))
				.isNotNull()
				.isNotEmpty();

			return this;
		}
	}
}