package org.erd;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Simulation {
	@NotNull(message = "Amount cannot be empty")
	@DecimalMin(value = "1", message = "Amount must be equal or greater than $ 1.000")
	@DecimalMax(value = "40", message = "Amount must be equal or less than than $ 40.000")
	private BigDecimal amount;

	@NotNull(message = "Installments cannot be empty")
	@Min(value = 2, message = "Installments must be equal or greater than 2")
	@Max(value = 48, message = "Installments must be equal or less than 48")
	private Integer installments;
	private String name;
	private String cpf;
	private String email;
	private boolean insurance;
}
