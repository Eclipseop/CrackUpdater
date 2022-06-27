package org.eclipseop.crackupdater.analyzer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AnalyzerInformation {
  String[] expectedFields();
}
