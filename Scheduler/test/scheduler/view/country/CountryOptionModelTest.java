/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.country;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import javafx.collections.ObservableList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import scheduler.dao.CityElement;
import scheduler.dao.CountryElement;
import scheduler.dao.DataRowState;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CountryOptionModelTest {
    private static HashMap<String, String> regionCodeToCountryNameMap;
    private static HashMap<String, String[]> regionCodeToResourceKeysMap;
    private static HashMap<String, Locale> resourceKeyToLocaleMap;
    private static HashMap<String, ZoneId> resourceKeyToZoneIdMap;
    private static HashMap<String, String> resourceKeyToNameMap;
    private static HashMap<String, String> resourceKeyToNativeNameMap;
    private Locale defaultLocale;
    
    public CountryOptionModelTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        final Locale defaultLocale = Locale.getDefault();
        Locale locale = Locale.forLanguageTag("en-US");
        try {
            if (!locale.equals(defaultLocale))
                Locale.setDefault(locale);
            regionCodeToCountryNameMap = new HashMap<>();
            regionCodeToCountryNameMap.put("US", "United States");
            regionCodeToCountryNameMap.put("EC", "Ecuador");
            regionCodeToCountryNameMap.put("GT", "Guatemala");
            regionCodeToCountryNameMap.put("HN", "Honduras");
            regionCodeToCountryNameMap.put("PR", "Puerto Rico");
            regionCodeToCountryNameMap.put("AU", "Australia");
            regionCodeToCountryNameMap.put("CH", "Switzerland");
            regionCodeToCountryNameMap.put("GB", "United Kingdom");
            regionCodeToCountryNameMap.put("DE", "Germany");
            regionCodeToCountryNameMap.put("IN", "India");
            regionCodeToResourceKeysMap = new HashMap<>();
            regionCodeToResourceKeysMap.put("US", new String[] { "las_vegas=en-US", "los_angeles", "san_francisco", "san_jose", "seattle", "denver",
                "phoenix", "salt_lake_city", "chicago", "new_orleans", "houston", "dallas", "oklahoma_city", "kansas_city", "minneapolis", "miami",
                "atlanta", "washington_dc", "philadelphia", "new_york", "boston", "detroit", "indianapolis" });
            regionCodeToResourceKeysMap.put("EC", new String[] { "quito" });
            regionCodeToResourceKeysMap.put("GT", new String[] { "guatemala_city" });
            regionCodeToResourceKeysMap.put("HN", new String[] { "tegucigalpa" });
            regionCodeToResourceKeysMap.put("PR", new String[] { "vieques", "san_juan" });
            regionCodeToResourceKeysMap.put("AU", new String[] { "canberra", "perth" });
            regionCodeToResourceKeysMap.put("CH", new String[] { "zurich" });
            regionCodeToResourceKeysMap.put("GB", new String[] { "london" });
            regionCodeToResourceKeysMap.put("DE", new String[] { "berlin", "armstedt", "frankfurt" });
            regionCodeToResourceKeysMap.put("IN", new String[] { "new_delhi", "bangalore", "mumbai", "kolkata", "chennai" });
            resourceKeyToLocaleMap = new HashMap<>();
            resourceKeyToLocaleMap.put("las_vegas", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("", Locale.forLanguageTag(""));
            resourceKeyToLocaleMap.put("los_angeles", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("san_francisco", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("san_jose", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("seattle", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("denver", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("phoenix", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("salt_lake_city", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("chicago", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("new_orleans", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("houston", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("dallas", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("oklahoma_city", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("kansas_city", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("minneapolis", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("miami", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("atlanta", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("washington_dc", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("philadelphia", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("new_york", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("boston", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("detroit", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("indianapolis", Locale.forLanguageTag("en-US"));
            resourceKeyToLocaleMap.put("quito", Locale.forLanguageTag("es-EC"));
            resourceKeyToLocaleMap.put("guatemala_city", Locale.forLanguageTag("es-GT"));
            resourceKeyToLocaleMap.put("tegucigalpa", Locale.forLanguageTag("es-HN"));
            resourceKeyToLocaleMap.put("vieques", Locale.forLanguageTag("es-PR"));
            resourceKeyToLocaleMap.put("san_juan", Locale.forLanguageTag("es-PR"));
            resourceKeyToLocaleMap.put("canberra", Locale.forLanguageTag("en-AU"));
            resourceKeyToLocaleMap.put("perth", Locale.forLanguageTag("en-AU"));
            resourceKeyToLocaleMap.put("zurich", Locale.forLanguageTag("de-CH"));
            resourceKeyToLocaleMap.put("london", Locale.forLanguageTag("en-GB"));
            resourceKeyToLocaleMap.put("berlin", Locale.forLanguageTag("de-DE"));
            resourceKeyToLocaleMap.put("armstedt", Locale.forLanguageTag("de-DE"));
            resourceKeyToLocaleMap.put("frankfurt", Locale.forLanguageTag("de-DE"));
            resourceKeyToLocaleMap.put("new_delhi", Locale.forLanguageTag("hi-IN"));
            resourceKeyToLocaleMap.put("bangalore", Locale.forLanguageTag("hi-IN"));
            resourceKeyToLocaleMap.put("mumbai", Locale.forLanguageTag("hi-IN"));
            resourceKeyToLocaleMap.put("kolkata", Locale.forLanguageTag("hi-IN"));
            resourceKeyToLocaleMap.put("chennai", Locale.forLanguageTag("hi-IN"));
            resourceKeyToZoneIdMap = new HashMap<>();
            resourceKeyToZoneIdMap.put("las_vegas", ZoneId.of("US/Pacific"));
            resourceKeyToZoneIdMap.put("los_angeles", ZoneId.of("US/Pacific"));
            resourceKeyToZoneIdMap.put("san_francisco", ZoneId.of("US/Pacific"));
            resourceKeyToZoneIdMap.put("san_jose", ZoneId.of("US/Pacific"));
            resourceKeyToZoneIdMap.put("seattle", ZoneId.of("US/Pacific"));
            resourceKeyToZoneIdMap.put("denver", ZoneId.of("US/Mountain"));
            resourceKeyToZoneIdMap.put("phoenix", ZoneId.of("US/Mountain"));
            resourceKeyToZoneIdMap.put("salt_lake_city", ZoneId.of("US/Mountain"));
            resourceKeyToZoneIdMap.put("chicago", ZoneId.of("US/Central"));
            resourceKeyToZoneIdMap.put("new_orleans", ZoneId.of("US/Central"));
            resourceKeyToZoneIdMap.put("houston", ZoneId.of("US/Central"));
            resourceKeyToZoneIdMap.put("dallas", ZoneId.of("US/Central"));
            resourceKeyToZoneIdMap.put("oklahoma_city", ZoneId.of("US/Central"));
            resourceKeyToZoneIdMap.put("kansas_city", ZoneId.of("US/Central"));
            resourceKeyToZoneIdMap.put("minneapolis", ZoneId.of("US/Central"));
            resourceKeyToZoneIdMap.put("miami", ZoneId.of("US/Eastern"));
            resourceKeyToZoneIdMap.put("atlanta", ZoneId.of("US/Eastern"));
            resourceKeyToZoneIdMap.put("washington_dc", ZoneId.of("US/Eastern"));
            resourceKeyToZoneIdMap.put("philadelphia", ZoneId.of("US/Eastern"));
            resourceKeyToZoneIdMap.put("new_york", ZoneId.of("US/Eastern"));
            resourceKeyToZoneIdMap.put("boston", ZoneId.of("US/Eastern"));
            resourceKeyToZoneIdMap.put("detroit", ZoneId.of("US/Eastern"));
            resourceKeyToZoneIdMap.put("indianapolis", ZoneId.of("US/Eastern"));
            resourceKeyToZoneIdMap.put("quito", ZoneId.of("America/Guayaquil"));
            resourceKeyToZoneIdMap.put("guatemala_city", ZoneId.of("America/Guatemala"));
            resourceKeyToZoneIdMap.put("tegucigalpa", ZoneId.of("America/Tegucigalpa"));
            resourceKeyToZoneIdMap.put("vieques", ZoneId.of("America/Puerto_Rico"));
            resourceKeyToZoneIdMap.put("san_juan", ZoneId.of("America/Puerto_Rico"));
            resourceKeyToZoneIdMap.put("canberra", ZoneId.of("Australia/ACT"));
            resourceKeyToZoneIdMap.put("perth", ZoneId.of("Australia/West"));
            resourceKeyToZoneIdMap.put("zurich", ZoneId.of("CET"));
            resourceKeyToZoneIdMap.put("london", ZoneId.of("GB"));
            resourceKeyToZoneIdMap.put("berlin", ZoneId.of("CET"));
            resourceKeyToZoneIdMap.put("armstedt", ZoneId.of("CET"));
            resourceKeyToZoneIdMap.put("frankfurt", ZoneId.of("CET"));
            resourceKeyToZoneIdMap.put("new_delhi", ZoneId.of("Asia/Kolkata"));
            resourceKeyToZoneIdMap.put("bangalore", ZoneId.of("Asia/Kolkata"));
            resourceKeyToZoneIdMap.put("mumbai", ZoneId.of("Asia/Kolkata"));
            resourceKeyToZoneIdMap.put("kolkata", ZoneId.of("Asia/Kolkata"));
            resourceKeyToZoneIdMap.put("chennai", ZoneId.of("Asia/Kolkata"));
            resourceKeyToNameMap = new HashMap<>();
            resourceKeyToNameMap.put("armstedt", "Armstedt");
            resourceKeyToNameMap.put("berlin", "Berlin");
            resourceKeyToNameMap.put("frankfurt", "Frankfurt");
            resourceKeyToNameMap.put("new_delhi", "New Delhi");
            resourceKeyToNameMap.put("bangalore", "Bangalore");
            resourceKeyToNameMap.put("mumbai", "Mumbai");
            resourceKeyToNameMap.put("kolkata", "Kolkata");
            resourceKeyToNameMap.put("chennai", "Chennai");
            resourceKeyToNameMap.put("las_vegas", "Las Vegas");
            resourceKeyToNameMap.put("los_angeles", "Los Angeles");
            resourceKeyToNameMap.put("san_francisco", "San Francisco");
            resourceKeyToNameMap.put("san_jose", "San Jose");
            resourceKeyToNameMap.put("seattle", "Seattle");
            resourceKeyToNameMap.put("denver", "Denver");
            resourceKeyToNameMap.put("phoenix", "Phoenix");
            resourceKeyToNameMap.put("salt_lake_city", "Salt Lake City");
            resourceKeyToNameMap.put("chicago", "Chicago");
            resourceKeyToNameMap.put("new_orleans", "New Orleans");
            resourceKeyToNameMap.put("houston", "Houston");
            resourceKeyToNameMap.put("dallas", "Dallas");
            resourceKeyToNameMap.put("oklahoma_city", "Oklahoma City");
            resourceKeyToNameMap.put("kansas_city", "Kansas City");
            resourceKeyToNameMap.put("minneapolis", "Minneapolis");
            resourceKeyToNameMap.put("miami", "Miami");
            resourceKeyToNameMap.put("atlanta", "Atlanta");
            resourceKeyToNameMap.put("washington_dc", "Washington, DC");
            resourceKeyToNameMap.put("philadelphia", "Philadelphia");
            resourceKeyToNameMap.put("new_york", "New York");
            resourceKeyToNameMap.put("boston", "Boston");
            resourceKeyToNameMap.put("detroit", "Detroit");
            resourceKeyToNameMap.put("indianapolis", "Indianapolis");
            resourceKeyToNameMap.put("tegucigalpa", "Tegucigalpa");
            resourceKeyToNameMap.put("san_juan", "San Juan");
            resourceKeyToNameMap.put("vieques", "Vieques");
            resourceKeyToNameMap.put("canberra", "Canberra");
            resourceKeyToNameMap.put("guatemala_city", "Guatemala City");
            resourceKeyToNameMap.put("london", "London");
            resourceKeyToNameMap.put("perth", "Perth");
            resourceKeyToNameMap.put("quito", "Quito");
            resourceKeyToNameMap.put("zurich", "Zurich");
            resourceKeyToNativeNameMap = new HashMap<>();
            resourceKeyToNativeNameMap.put("las_vegas", "Las Vegas");
            resourceKeyToNativeNameMap.put("los_angeles", "Los Angeles");
            resourceKeyToNativeNameMap.put("san_francisco", "San Francisco");
            resourceKeyToNativeNameMap.put("seattle", "Seattle");
            resourceKeyToNativeNameMap.put("denver", "Denver");
            resourceKeyToNativeNameMap.put("phoenix", "Phoenix");
            resourceKeyToNativeNameMap.put("salt_lake_city", "Salt Lake City");
            resourceKeyToNativeNameMap.put("chicago", "Chicago");
            resourceKeyToNativeNameMap.put("new_orleans", "New Orleans");
            resourceKeyToNativeNameMap.put("houston", "Houston");
            resourceKeyToNativeNameMap.put("dallas", "Dallas");
            resourceKeyToNativeNameMap.put("oklahoma_city", "Oklahoma City");
            resourceKeyToNativeNameMap.put("kansas_city", "Kansas City");
            resourceKeyToNativeNameMap.put("minneapolis", "Minneapolis");
            resourceKeyToNativeNameMap.put("miami", "Miami");
            resourceKeyToNativeNameMap.put("atlanta", "Atlanta");
            resourceKeyToNativeNameMap.put("washington_dc", "Washington, DC");
            resourceKeyToNativeNameMap.put("philadelphia", "Philadelphia");
            resourceKeyToNativeNameMap.put("new_york", "New York");
            resourceKeyToNativeNameMap.put("boston", "Boston");
            resourceKeyToNativeNameMap.put("detroit", "Detroit");
            resourceKeyToNativeNameMap.put("indianapolis", "Indianapolis");
            resourceKeyToNativeNameMap.put("canberra", "Canberra");
            resourceKeyToNativeNameMap.put("london", "London");
            resourceKeyToNativeNameMap.put("perth", "Perth");
            resourceKeyToNativeNameMap.put("armstedt", "Armstedt");
            resourceKeyToNativeNameMap.put("berlin", "Berlin");
            resourceKeyToNativeNameMap.put("frankfurt", "Frankfurt");
            resourceKeyToNativeNameMap.put("zurich", "Zürich");
            resourceKeyToNativeNameMap.put("new_delhi", "नई दिल्ली");
            resourceKeyToNativeNameMap.put("bangalore", "बैंगलोर");
            resourceKeyToNativeNameMap.put("mumbai", "मुंबई");
            resourceKeyToNativeNameMap.put("kolkata", "कोलकाता");
            resourceKeyToNativeNameMap.put("chennai", "चेन्नई");
            resourceKeyToNativeNameMap.put("san_jose", "San Jose");
            resourceKeyToNativeNameMap.put("tegucigalpa", "Tegucigalpa");
            resourceKeyToNativeNameMap.put("san_juan", "San Juan");
            resourceKeyToNativeNameMap.put("vieques", "Vieques");
            resourceKeyToNativeNameMap.put("guatemala_city", "Ciudad de Guatemala");
            resourceKeyToNativeNameMap.put("quito", "Quito");
        } finally {
            if (!defaultLocale.equals(Locale.getDefault())) {
                Locale.setDefault(defaultLocale);
            }
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    
    @Before
    public void setUp() {
        defaultLocale = Locale.getDefault();
        Locale locale = Locale.forLanguageTag("en-US");
        if (!locale.equals(defaultLocale))
            Locale.setDefault(locale);
    }
    
    @After
    public void tearDown() {
        if (!defaultLocale.equals(Locale.getDefault())) {
            Locale.setDefault(defaultLocale);
        }
    }

    /**
     * Test of getCountryOption method, of class CountryOptionModel.
     */
    @Test
    @SuppressWarnings("can-use-functional-operations")
    public void testGetCountryOption() {
        for (String regionCode : regionCodeToCountryNameMap.keySet()) {
            String expResult = regionCodeToCountryNameMap.get(regionCode);
            CountryOptionModel result = CountryOptionModel.getCountryOption(regionCode);
            assertNotNull(regionCode, result);
            assertEquals(regionCode, result.getRegionCode());
            assertEquals(regionCode, expResult, result.getName());
        }
    }

    /**
     * Test of getCountryOptions method, of class CountryOptionModel.
     */
    @Test
    public void testGetCountryOptions() {
        ObservableList<CountryOptionModel> resultList = CountryOptionModel.getCountryOptions();
        
        assertEquals(regionCodeToCountryNameMap.size(), resultList.size());
        
        HashSet<String> found = new HashSet<>();
        for (int i = 0; i < resultList.size(); i++) {
            CountryOptionModel resultItem = resultList.get(i);
            String msg = Integer.toString(i);
            assertNotNull(msg, resultItem);
            String regionCode = resultItem.getRegionCode();
            assertNotNull(msg, regionCode);
            assertTrue(msg, regionCodeToCountryNameMap.containsKey(regionCode));
            assertFalse(msg, found.contains(regionCode));
            found.add(regionCode);
            assertEquals(regionCodeToCountryNameMap.get(regionCode), resultItem.getName());
            assertEquals(msg, Integer.MIN_VALUE, resultItem.getPrimaryKey());
            CountryElement dataObject = resultItem.getDataObject();
            assertNotNull(msg, dataObject);
            assertEquals(msg, regionCode, dataObject.getName());
            assertEquals(msg, DataRowState.UNMODIFIED, dataObject.getRowState());
            assertEquals(msg, Integer.MIN_VALUE, dataObject.getPrimaryKey());
        }
        
        assertEquals(resultList.size(), found.size());
    }

    /**
     * Test of getCities method, of class CountryOptionModel.
     */
    @Test
    public void testGetCities() {
        for (String regionCode : regionCodeToResourceKeysMap.keySet()) {
            CountryOptionModel country = CountryOptionModel.getCountryOption(regionCode);
            if (null == country)
                fail(String.format("Inconclusive: Failed to get country with region code %s", regionCode));
            @SuppressWarnings("null")
            ObservableList<CityOptionModel> resultList = country.getCities();
            assertNotNull(regionCode, resultList);
            String[] expResult = regionCodeToResourceKeysMap.get(regionCode);
            assertEquals(regionCode, expResult.length, resultList.size());
            HashSet<String> found = new HashSet<>();
            for (int i = 0; i < resultList.size(); i++) {
                CityOptionModel resultItem = resultList.get(i);
                String msg = String.format("%s[%d]", regionCode, i);
                assertNotNull(msg, resultItem);
                String resourceKey = resultItem.getResourceKey();
                assertNotNull(msg, resourceKey);
                assertTrue(msg, resourceKeyToNameMap.containsKey(resourceKey));
                assertFalse(msg, found.contains(resourceKey));
                found.add(resourceKey);
                CityCountryModel<? extends CountryElement> resultCountry = resultItem.getCountry();
                assertNotNull(msg, resultCountry);
                assertSame(country, resultCountry);
                assertEquals(msg, Integer.MIN_VALUE, resultItem.getPrimaryKey());
                assertEquals(msg, resourceKeyToNameMap.get(resourceKey), resultItem.getName());
                assertEquals(msg, resourceKeyToNativeNameMap.get(resourceKey), resultItem.getNativeName());
                Locale resultLocale = resultItem.getLocale();
                Locale expLocale = resourceKeyToLocaleMap.get(resourceKey);
                assertNotNull(msg, resultLocale);
                assertEquals(msg, expLocale.toLanguageTag(), resultLocale.toLanguageTag());
                assertEquals(msg, expLocale.getDisplayCountry(), resultItem.getCountryName());
                assertEquals(msg, expLocale.getDisplayCountry(resultLocale), resultItem.getNativeCountryName());
                ZoneId resultZoneId = resultItem.getZoneId();
                assertNotNull(msg, resultLocale);
                ZoneId expZoneId = resourceKeyToZoneIdMap.get(resourceKey);
                assertEquals(msg, expZoneId.getId(), resultZoneId.getId());
                CityElement dataObject = resultItem.getDataObject();
                assertNotNull(msg, dataObject);
                CountryElement ce = dataObject.getCountry();
                assertNotNull(msg, ce);
                assertSame(country.getDataObject(), ce);
                assertEquals(msg, resourceKey, dataObject.getName());
                assertEquals(msg, DataRowState.UNMODIFIED, dataObject.getRowState());
                assertEquals(msg, Integer.MIN_VALUE, dataObject.getPrimaryKey());
            }
        
            assertEquals(resultList.size(), found.size());
        }
    }

}
