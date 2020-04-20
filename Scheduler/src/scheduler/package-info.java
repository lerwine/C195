/**
 * Top-level package for the Scheduler application.<p>
 * Application Resources:</p>
 * <ul>
 * <li><a href="#globalConfig">Global Configuration</a></li>
 * <li><a href="#supportedCities">Supported Cities</a></li>
 * <li><a href="#App">{@code App} resource bundle</a></li>
 * </ul>
 *
 * <h2 id="globalConfig">Global Configuration</h2>
 * File is located at {@code /resources/scheduler/config.properties}.
 * <dl>
 * <dt>businessHoursStart</dt>
 * <dd>Time of day for start of business hours.</dd>
 * <dt>businessHoursDuration</dt>
 * <dd>Duration of business hours in minutes.</dd>
 * <dt>appointmentAlertLeadTime</dt>
 * <dd>Lead alert time for impending appointments in minutes.</dd>
 * <dt>dbServerName</dt>
 * <dd>The name of the database server to connect to.</dd>
 * <dt>dbName</dt>
 * <dd>The name of the database to open.</dd>
 * <dt>dbLogin</dt>
 * <dd>The login name to use when connecting to the database server.</dd>
 * <dt>dbPassword</dt>
 * <dd>The password to use when connecting to the database server.</dd>
 * </dl>
 *
 * <h2 id="supportedCities">Supported Cities</h2>
 * This consists of a standalone {@code properties file} located at {@code /resources/scheduler/supportedCities.properties} and a resource bundle with
 * the base name {@code /resources/scheduler/cityNames}. Every key in the {@code supportedCities.properties} file needs to have a corresponding entry
 * with the same key in the resource bundle. The {@code supportedCities.properties} defines the language, country and time zone for each city, and the
 * {@code cityNames} resource bundle contains the language-specific display name for each of those cities.
 *
 * <h2 id="App">{@code App} resource bundle</h2>
 * This resource bundle contains language-specific strings that are not necessarily specific to any particular view.
 */
package scheduler;
