/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.country;

import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import scheduler.dao.CityDAO;
import scheduler.dao.CityElement;
import scheduler.dao.CountryDAO;
import scheduler.dao.CountryElement;
import scheduler.dao.DataRowState;
import scheduler.view.city.CityModel;
import scheduler.view.city.CityModelImpl;
import static scheduler.view.country.CountryOptionModelTest.getRegionCodeToResourceKeysMap;
import static scheduler.view.country.CountryOptionModelTest.getResourceKeyToLocaleMap;
import static scheduler.view.country.CountryOptionModelTest.getResourceKeyToNameMap;
import static scheduler.view.country.CountryOptionModelTest.getResourceKeyToNativeNameMap;
import static scheduler.view.country.CountryOptionModelTest.getResourceKeyToZoneIdMap;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CityOptionModelTest {

    private static HashMap<String, String[]> regionCodeToResourceKeysMap;
    private static HashMap<String, Locale> resourceKeyToLocaleMap;
    private static HashMap<String, ZoneId> resourceKeyToZoneIdMap;
    private static HashMap<String, String> resourceKeyToNameMap;
    private static HashMap<String, String> resourceKeyToNativeNameMap;

    private Locale defaultLocale;
    
    public CityOptionModelTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        final Locale defaultLocale = Locale.getDefault();
        Locale locale = Locale.forLanguageTag("en-US");
        try {
            if (!locale.equals(defaultLocale))
                Locale.setDefault(locale);
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
     * Test of getCityOptionMap method, of class CityOptionModel.
     */
    @Test
    public void testGetCityOptionMap() {
        ObservableMap<String, CityOptionModel> result = CityOptionModel.getCityOptionMap();
        assertEquals(resourceKeyToLocaleMap.size(), result.size());
        HashMap<String, Set<String>> found = new HashMap<>();
        for (String resourceKey : result.keySet()) {
            CityOptionModel optionModel = result.get(resourceKey);
            assertNotNull(resourceKey, optionModel);
            assertEquals(resourceKey, resourceKey, optionModel.getResourceKey());
            assertTrue(resourceKey, resourceKeyToLocaleMap.containsKey(resourceKey));
            Locale locale = optionModel.getLocale();
            assertNotNull(resourceKey, locale);
            String regionCode = locale.getCountry();
            if (found.containsKey(regionCode)) {
                Set<String> set = found.get(regionCode);
                assertFalse(resourceKey, set.contains(resourceKey));
                set.add(resourceKey);
            } else {
                Set<String> set = new HashSet<>();
                set.add(resourceKey);
                found.put(regionCode, set);
            }
            assertEquals(resourceKey, resourceKeyToLocaleMap.get(resourceKey).toLanguageTag(), locale.toLanguageTag());
            ZoneId zoneId = optionModel.getZoneId();
            assertNotNull(resourceKey, zoneId);
            assertEquals(resourceKey, resourceKeyToZoneIdMap.get(resourceKey).getId(), zoneId.getId());
            String name = optionModel.getName();
            assertEquals(resourceKey, resourceKeyToNameMap.get(resourceKey), name);
            name = optionModel.getNativeName();
            assertEquals(resourceKey, resourceKeyToNativeNameMap.get(resourceKey), name);
            CityCountryModel<? extends CountryElement> countryModel = optionModel.getCountry();
            assertNotNull(resourceKey, countryModel);
            assertTrue(resourceKey, countryModel instanceof CountryOptionModel);
            assertEquals(resourceKey, regionCode, ((CountryOptionModel)countryModel).getRegionCode());
        }
        
        assertEquals(regionCodeToResourceKeysMap.size(), found.size());
        for (String regionCode : found.keySet()) {
            assertTrue(regionCode, regionCodeToResourceKeysMap.containsKey(regionCode));
            String[] expectedKeys = regionCodeToResourceKeysMap.get(regionCode);
            Set<String> resultKeys = found.get(regionCode);
            assertEquals(regionCode, expectedKeys.length, resultKeys.size());
            for (String resourceKey : expectedKeys) {
                assertTrue(String.format("%s/%s", regionCode, resourceKey), resultKeys.contains(resourceKey));
            }
        }
    }

    /**
     * Test of getCityOptions method, of class CityOptionModel.
     */
    @Test
    public void testGetCityOptions() {
        System.out.println("getCityOptions");
        Collection<CityDAO> dataObjects = FXCollections.observableArrayList();
        CityDAO.FactoryImpl factory = CityDAO.getFactory();
        CityDAO dao = factory.createNew();
        dao.setName("las_vegas");
        dao.setCountry(CountryOptionModel.getCountryOption("US").getDataObject());
        dataObjects.add(dao);
        
        dao = factory.createNew();
        dao.setName("washington_dc");
        dao.setCountry(CountryOptionModel.getCountryOption("US").getDataObject());
        dataObjects.add(dao);
        
        dao = factory.createNew();
        dao.setName("quito");
        dao.setCountry(CountryOptionModel.getCountryOption("EC").getDataObject());
        dataObjects.add(dao);
        
        dao = factory.createNew();
        dao.setName("guatemala_city");
        dao.setCountry(CountryOptionModel.getCountryOption("GT").getDataObject());
        dataObjects.add(dao);
        
        dao = factory.createNew();
        dao.setName("san_juan");
        dao.setCountry(CountryOptionModel.getCountryOption("PR").getDataObject());
        dataObjects.add(dao);
        
        dao = factory.createNew();
        dao.setName("perth");
        dao.setCountry(CountryOptionModel.getCountryOption("AU").getDataObject());
        dataObjects.add(dao);
        
        dao = factory.createNew();
        dao.setName("new_delhi");
        dao.setCountry(CountryOptionModel.getCountryOption("IN").getDataObject());
        dataObjects.add(dao);
        
        Stream<CityModel<? extends CityElement>> result = CityOptionModel.getCityOptions(dataObjects);
        HashSet<String> found = new HashSet<>();
        Iterator<CityModel<? extends CityElement>> iterator = result.iterator();
        while (iterator.hasNext()) {
            CityModel<? extends CityElement> resultItem = iterator.next();
            String msg = Integer.toString(found.size());
            assertNotNull(msg, resultItem);
            CityOptionModel optionModel = resultItem.getOptionModel();
            assertNotNull(msg, optionModel);
            String resourceKey = optionModel.getResourceKey();
            assertNotNull(msg, resourceKey);
            assertTrue(msg, resourceKeyToLocaleMap.containsKey(resourceKey));
            assertFalse(msg, found.contains(resourceKey));
            found.add(resourceKey);
            Locale locale = optionModel.getLocale();
            assertNotNull(resourceKey, locale);
            String regionCode = locale.getCountry();
            assertEquals(resourceKey, resourceKeyToLocaleMap.get(resourceKey).toLanguageTag(), locale.toLanguageTag());
            ZoneId zoneId = optionModel.getZoneId();
            assertNotNull(resourceKey, zoneId);
            assertEquals(resourceKey, resourceKeyToZoneIdMap.get(resourceKey).getId(), zoneId.getId());
            String name = optionModel.getName();
            assertEquals(resourceKey, resourceKeyToNameMap.get(resourceKey), name);
            name = optionModel.getNativeName();
            assertEquals(resourceKey, resourceKeyToNativeNameMap.get(resourceKey), name);
            name = resultItem.getName();
            assertEquals(resourceKey, resourceKeyToNameMap.get(resourceKey), name);
            CityCountryModel<? extends CountryElement> countryModel = resultItem.getCountry();
            assertNotNull(resourceKey, countryModel);
            CityElement dataObject = resultItem.getDataObject();
            assertNotNull(msg, dataObject);
            assertEquals(msg, resourceKey, dataObject.getName());
            assertEquals(msg, Integer.MIN_VALUE, dataObject.getPrimaryKey());
            switch (resourceKey) {
                case "las_vegas":
                case "washington_dc":
                case "quito":
                case "guatemala_city":
                case "san_juan":
                case "perth":
                case "new_delhi":
                    assertEquals(msg, DataRowState.NEW, dataObject.getRowState());
                    break;
                default:
                    assertEquals(msg, DataRowState.UNMODIFIED, dataObject.getRowState());
                    break;
            }
        }
        assertEquals(regionCodeToResourceKeysMap.keySet().stream().mapToInt((t) -> regionCodeToResourceKeysMap.get(t).length).sum(), found.size());
    }

}
