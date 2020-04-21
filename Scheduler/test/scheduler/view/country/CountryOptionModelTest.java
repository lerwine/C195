/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.country;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import scheduler.dao.CityElement;
import scheduler.dao.CountryDAO;
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
            regionCodeToCountryNameMap = getRegionCodeToCountryNameMap();
            regionCodeToResourceKeysMap = getRegionCodeToResourceKeysMap();
            resourceKeyToLocaleMap = getResourceKeyToLocaleMap();
            resourceKeyToZoneIdMap = getResourceKeyToZoneIdMap();
            resourceKeyToNameMap = getResourceKeyToNameMap();
            resourceKeyToNativeNameMap = getResourceKeyToNativeNameMap();
        } finally {
            if (!defaultLocale.equals(Locale.getDefault())) {
                Locale.setDefault(defaultLocale);
            }
        }
    }

    public static HashMap<String, String[]> getRegionCodeToResourceKeysMap() {
        HashMap<String, String[]> map = new HashMap<>();
        map.put("US", new String[] { "las_vegas", "los_angeles", "san_francisco", "san_jose", "seattle", "denver",
            "phoenix", "salt_lake_city", "chicago", "new_orleans", "houston", "dallas", "oklahoma_city", "kansas_city", "minneapolis", "miami",
            "atlanta", "washington_dc", "philadelphia", "new_york", "boston", "detroit", "indianapolis" });
        map.put("EC", new String[] { "quito" });
        map.put("GT", new String[] { "guatemala_city" });
        map.put("HN", new String[] { "tegucigalpa" });
        map.put("PR", new String[] { "vieques", "san_juan" });
        map.put("AU", new String[] { "canberra", "perth" });
        map.put("CH", new String[] { "zurich" });
        map.put("GB", new String[] { "london" });
        map.put("DE", new String[] { "berlin", "armstedt", "frankfurt" });
        map.put("IN", new String[] { "new_delhi", "bangalore", "mumbai", "kolkata", "chennai" });
        return map;
    }

    public static HashMap<String, Locale> getResourceKeyToLocaleMap() {
        HashMap<String, Locale> map = new HashMap<>();
        map.put("las_vegas", Locale.forLanguageTag("en-US"));
        map.put("los_angeles", Locale.forLanguageTag("en-US"));
        map.put("san_francisco", Locale.forLanguageTag("en-US"));
        map.put("san_jose", Locale.forLanguageTag("en-US"));
        map.put("seattle", Locale.forLanguageTag("en-US"));
        map.put("denver", Locale.forLanguageTag("en-US"));
        map.put("phoenix", Locale.forLanguageTag("en-US"));
        map.put("salt_lake_city", Locale.forLanguageTag("en-US"));
        map.put("chicago", Locale.forLanguageTag("en-US"));
        map.put("new_orleans", Locale.forLanguageTag("en-US"));
        map.put("houston", Locale.forLanguageTag("en-US"));
        map.put("dallas", Locale.forLanguageTag("en-US"));
        map.put("oklahoma_city", Locale.forLanguageTag("en-US"));
        map.put("kansas_city", Locale.forLanguageTag("en-US"));
        map.put("minneapolis", Locale.forLanguageTag("en-US"));
        map.put("miami", Locale.forLanguageTag("en-US"));
        map.put("atlanta", Locale.forLanguageTag("en-US"));
        map.put("washington_dc", Locale.forLanguageTag("en-US"));
        map.put("philadelphia", Locale.forLanguageTag("en-US"));
        map.put("new_york", Locale.forLanguageTag("en-US"));
        map.put("boston", Locale.forLanguageTag("en-US"));
        map.put("detroit", Locale.forLanguageTag("en-US"));
        map.put("indianapolis", Locale.forLanguageTag("en-US"));
        map.put("quito", Locale.forLanguageTag("es-EC"));
        map.put("guatemala_city", Locale.forLanguageTag("es-GT"));
        map.put("tegucigalpa", Locale.forLanguageTag("es-HN"));
        map.put("vieques", Locale.forLanguageTag("es-PR"));
        map.put("san_juan", Locale.forLanguageTag("es-PR"));
        map.put("canberra", Locale.forLanguageTag("en-AU"));
        map.put("perth", Locale.forLanguageTag("en-AU"));
        map.put("zurich", Locale.forLanguageTag("de-CH"));
        map.put("london", Locale.forLanguageTag("en-GB"));
        map.put("berlin", Locale.forLanguageTag("de-DE"));
        map.put("armstedt", Locale.forLanguageTag("de-DE"));
        map.put("frankfurt", Locale.forLanguageTag("de-DE"));
        map.put("new_delhi", Locale.forLanguageTag("hi-IN"));
        map.put("bangalore", Locale.forLanguageTag("hi-IN"));
        map.put("mumbai", Locale.forLanguageTag("hi-IN"));
        map.put("kolkata", Locale.forLanguageTag("hi-IN"));
        map.put("chennai", Locale.forLanguageTag("hi-IN"));
        return map;
    }

    public static HashMap<String, ZoneId> getResourceKeyToZoneIdMap() {
        HashMap<String, ZoneId> map = new HashMap<>();
        map.put("las_vegas", ZoneId.of("US/Pacific"));
        map.put("los_angeles", ZoneId.of("US/Pacific"));
        map.put("san_francisco", ZoneId.of("US/Pacific"));
        map.put("san_jose", ZoneId.of("US/Pacific"));
        map.put("seattle", ZoneId.of("US/Pacific"));
        map.put("denver", ZoneId.of("US/Mountain"));
        map.put("phoenix", ZoneId.of("US/Mountain"));
        map.put("salt_lake_city", ZoneId.of("US/Mountain"));
        map.put("chicago", ZoneId.of("US/Central"));
        map.put("new_orleans", ZoneId.of("US/Central"));
        map.put("houston", ZoneId.of("US/Central"));
        map.put("dallas", ZoneId.of("US/Central"));
        map.put("oklahoma_city", ZoneId.of("US/Central"));
        map.put("kansas_city", ZoneId.of("US/Central"));
        map.put("minneapolis", ZoneId.of("US/Central"));
        map.put("miami", ZoneId.of("US/Eastern"));
        map.put("atlanta", ZoneId.of("US/Eastern"));
        map.put("washington_dc", ZoneId.of("US/Eastern"));
        map.put("philadelphia", ZoneId.of("US/Eastern"));
        map.put("new_york", ZoneId.of("US/Eastern"));
        map.put("boston", ZoneId.of("US/Eastern"));
        map.put("detroit", ZoneId.of("US/Eastern"));
        map.put("indianapolis", ZoneId.of("US/Eastern"));
        map.put("quito", ZoneId.of("America/Guayaquil"));
        map.put("guatemala_city", ZoneId.of("America/Guatemala"));
        map.put("tegucigalpa", ZoneId.of("America/Tegucigalpa"));
        map.put("vieques", ZoneId.of("America/Puerto_Rico"));
        map.put("san_juan", ZoneId.of("America/Puerto_Rico"));
        map.put("canberra", ZoneId.of("Australia/ACT"));
        map.put("perth", ZoneId.of("Australia/West"));
        map.put("zurich", ZoneId.of("CET"));
        map.put("london", ZoneId.of("GB"));
        map.put("berlin", ZoneId.of("CET"));
        map.put("armstedt", ZoneId.of("CET"));
        map.put("frankfurt", ZoneId.of("CET"));
        map.put("new_delhi", ZoneId.of("Asia/Kolkata"));
        map.put("bangalore", ZoneId.of("Asia/Kolkata"));
        map.put("mumbai", ZoneId.of("Asia/Kolkata"));
        map.put("kolkata", ZoneId.of("Asia/Kolkata"));
        map.put("chennai", ZoneId.of("Asia/Kolkata"));
        return map;
    }

    public static HashMap<String, String> getResourceKeyToNameMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("armstedt", "Armstedt");
        map.put("berlin", "Berlin");
        map.put("frankfurt", "Frankfurt");
        map.put("new_delhi", "New Delhi");
        map.put("bangalore", "Bangalore");
        map.put("mumbai", "Mumbai");
        map.put("kolkata", "Kolkata");
        map.put("chennai", "Chennai");
        map.put("las_vegas", "Las Vegas");
        map.put("los_angeles", "Los Angeles");
        map.put("san_francisco", "San Francisco");
        map.put("san_jose", "San Jose");
        map.put("seattle", "Seattle");
        map.put("denver", "Denver");
        map.put("phoenix", "Phoenix");
        map.put("salt_lake_city", "Salt Lake City");
        map.put("chicago", "Chicago");
        map.put("new_orleans", "New Orleans");
        map.put("houston", "Houston");
        map.put("dallas", "Dallas");
        map.put("oklahoma_city", "Oklahoma City");
        map.put("kansas_city", "Kansas City");
        map.put("minneapolis", "Minneapolis");
        map.put("miami", "Miami");
        map.put("atlanta", "Atlanta");
        map.put("washington_dc", "Washington, DC");
        map.put("philadelphia", "Philadelphia");
        map.put("new_york", "New York");
        map.put("boston", "Boston");
        map.put("detroit", "Detroit");
        map.put("indianapolis", "Indianapolis");
        map.put("tegucigalpa", "Tegucigalpa");
        map.put("san_juan", "San Juan");
        map.put("vieques", "Vieques");
        map.put("canberra", "Canberra");
        map.put("guatemala_city", "Guatemala City");
        map.put("london", "London");
        map.put("perth", "Perth");
        map.put("quito", "Quito");
        map.put("zurich", "Zurich");
        return map;
    }

    public static HashMap<String, String> getResourceKeyToNativeNameMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("las_vegas", "Las Vegas");
        map.put("los_angeles", "Los Angeles");
        map.put("san_francisco", "San Francisco");
        map.put("seattle", "Seattle");
        map.put("denver", "Denver");
        map.put("phoenix", "Phoenix");
        map.put("salt_lake_city", "Salt Lake City");
        map.put("chicago", "Chicago");
        map.put("new_orleans", "New Orleans");
        map.put("houston", "Houston");
        map.put("dallas", "Dallas");
        map.put("oklahoma_city", "Oklahoma City");
        map.put("kansas_city", "Kansas City");
        map.put("minneapolis", "Minneapolis");
        map.put("miami", "Miami");
        map.put("atlanta", "Atlanta");
        map.put("washington_dc", "Washington, DC");
        map.put("philadelphia", "Philadelphia");
        map.put("new_york", "New York");
        map.put("boston", "Boston");
        map.put("detroit", "Detroit");
        map.put("indianapolis", "Indianapolis");
        map.put("canberra", "Canberra");
        map.put("london", "London");
        map.put("perth", "Perth");
        map.put("armstedt", "Armstedt");
        map.put("berlin", "Berlin");
        map.put("frankfurt", "Frankfurt");
        map.put("zurich", "Zürich");
        map.put("new_delhi", "नई दिल्ली");
        map.put("bangalore", "बैंगलोर");
        map.put("mumbai", "मुंबई");
        map.put("kolkata", "कोलकाता");
        map.put("chennai", "चेन्नई");
        map.put("san_jose", "San Jose");
        map.put("tegucigalpa", "Tegucigalpa");
        map.put("san_juan", "San Juan");
        map.put("vieques", "Vieques");
        map.put("guatemala_city", "Ciudad de Guatemala");
        map.put("quito", "Quito");
        return map;
    }

    public static HashMap<String, String> getRegionCodeToCountryNameMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("US", "United States");
        map.put("EC", "Ecuador");
        map.put("GT", "Guatemala");
        map.put("HN", "Honduras");
        map.put("PR", "Puerto Rico");
        map.put("AU", "Australia");
        map.put("CH", "Switzerland");
        map.put("GB", "United Kingdom");
        map.put("DE", "Germany");
        map.put("IN", "India");
        return map;
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
     * Test of getCountryOption method, of class CountryOptionModel.
     */
    @Test
    @SuppressWarnings("can-use-functional-operations")
    public void testGetCountryOptions2() {
        CountryDAO.FactoryImpl factory = CountryDAO.getFactory();
        ObservableList<CountryDAO> dataObjects = FXCollections.observableArrayList();
        CountryDAO dao = factory.createNew();
        dao.setName("US");
        dataObjects.add(dao);
        dao = factory.createNew();
        dao.setName("DE");
        dataObjects.add(dao);
        dao = factory.createNew();
        dao.setName("IN");
        dataObjects.add(dao);
        dao = factory.createNew();
        dao.setName("PR");
        dataObjects.add(dao);
        Stream<CityCountryModel<? extends CountryElement>> resultStream = CountryOptionModel.getCountryOptions(dataObjects);
        
        HashSet<String> found = new HashSet<>();
        Iterator<CityCountryModel<? extends CountryElement>> iterator = resultStream.iterator();
        while (iterator.hasNext()) {
            CityCountryModel<? extends CountryElement> resultItem = iterator.next();
            String msg = Integer.toString(found.size());
            assertNotNull(msg, resultItem);
            String regionCode = resultItem.getOptionModel().getRegionCode();
            assertNotNull(msg, regionCode);
            assertTrue(msg, regionCodeToCountryNameMap.containsKey(regionCode));
            assertFalse(msg, found.contains(regionCode));
            found.add(regionCode);
            assertEquals(regionCodeToCountryNameMap.get(regionCode), resultItem.getName());
            assertEquals(msg, Integer.MIN_VALUE, resultItem.getPrimaryKey());
            CountryElement dataObject = resultItem.getDataObject();
            assertNotNull(msg, dataObject);
            assertEquals(msg, regionCode, dataObject.getName());
            assertEquals(msg, Integer.MIN_VALUE, dataObject.getPrimaryKey());
            switch (regionCode) {
                case "US":
                case "DE":
                case "IN":
                case "PR":
                    assertEquals(msg, DataRowState.NEW, dataObject.getRowState());
                    break;
                default:
                    assertEquals(msg, DataRowState.UNMODIFIED, dataObject.getRowState());
                    break;
            }
        }
        
        assertEquals(regionCodeToCountryNameMap.size(), found.size());
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
                assertEquals(msg, expLocale.getCountry(), country.getRegionCode());
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
