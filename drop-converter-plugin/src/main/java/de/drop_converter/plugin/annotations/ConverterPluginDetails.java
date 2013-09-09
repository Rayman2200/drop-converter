/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.plugin.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Additional plugin details that will be displayed in the plugin configuration directory.
 * 
 * @author Thomas Chojecki
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConverterPluginDetails {

  /** @return the name of the author */
  String authorName();

  /** @return the email of the author */
  String authorEmail() default "";

  /** @return the name of the plugin */
  String pluginName();

  /** @return the description of the plugin */
  String pluginDescription() default "";

  /** @return the version of the plugin. Default value is 1.0.0. */
  String pluginVersion();

  /** @return the website url as String of the plugin */
  String pluginWebsite() default "";

}
