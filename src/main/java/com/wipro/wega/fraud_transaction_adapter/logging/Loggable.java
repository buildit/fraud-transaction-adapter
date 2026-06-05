package com.wipro.wega.fraud_transaction_adapter.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as part of a logged journey. {@link LoggingAspect} emits
 * ENTRY and EXIT markers (with method name and elapsed time) around any method
 * annotated with this, keeping the logging concern out of the business code.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
}
