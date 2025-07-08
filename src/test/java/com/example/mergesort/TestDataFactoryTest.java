package com.example.mergesort;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for TestDataFactory to verify test data creation matches COBOL implementation.
 */
class TestDataFactoryTest {
    
    @Test
    void testCreateEastRegionData() {
        List<CustomerRecord> eastData = TestDataFactory.createEastRegionData();
        
        assertEquals(6, eastData.size());
        
        int[] expectedIds = {1, 5, 10, 50, 25, 75};
        int[] expectedContractIds = {5423, 12323, 653, 5050, 7725, 1175};
        String[] expectedLastNames = {"last-1", "last-5", "last-10", "last-50", "last-25", "last-75"};
        
        for (int i = 0; i < 6; i++) {
            assertEquals(expectedIds[i], eastData.get(i).getCustomerId());
            assertEquals(expectedContractIds[i], eastData.get(i).getCustomerContractId());
            assertEquals(expectedLastNames[i], eastData.get(i).getCustomerLastName());
        }
    }
    
    @Test
    void testCreateWestRegionData() {
        List<CustomerRecord> westData = TestDataFactory.createWestRegionData();
        
        assertEquals(5, westData.size());
        
        int[] expectedIds = {999, 3, 30, 85, 24};
        int[] expectedContractIds = {1610, 3331, 8765, 4567, 247};
        String[] expectedLastNames = {"last-999", "last-03", "last-30", "last-85", "last-24"};
        
        for (int i = 0; i < 5; i++) {
            assertEquals(expectedIds[i], westData.get(i).getCustomerId());
            assertEquals(expectedContractIds[i], westData.get(i).getCustomerContractId());
            assertEquals(expectedLastNames[i], westData.get(i).getCustomerLastName());
        }
    }
    
    @Test
    void testCreateEmptyData() {
        List<CustomerRecord> emptyData = TestDataFactory.createEmptyData();
        assertTrue(emptyData.isEmpty());
    }
    
    @Test
    void testCreateSingleRecordData() {
        List<CustomerRecord> singleData = TestDataFactory.createSingleRecordData();
        
        assertEquals(1, singleData.size());
        assertEquals(100, singleData.get(0).getCustomerId());
        assertEquals(9999, singleData.get(0).getCustomerContractId());
    }
    
    @Test
    void testCreateDuplicateIdData() {
        List<CustomerRecord> duplicateData = TestDataFactory.createDuplicateIdData();
        
        assertEquals(3, duplicateData.size());
        assertEquals(1, duplicateData.get(0).getCustomerId());
        assertEquals(1, duplicateData.get(1).getCustomerId());
        assertEquals(2, duplicateData.get(2).getCustomerId());
        
        assertNotEquals(duplicateData.get(0).getCustomerContractId(), duplicateData.get(1).getCustomerContractId());
    }
    
    @Test
    void testCreateLargeDataSet() {
        int size = 1000;
        List<CustomerRecord> largeData = TestDataFactory.createLargeDataSet(size);
        
        assertEquals(size, largeData.size());
        
        for (int i = 0; i < size; i++) {
            assertEquals(i + 1, largeData.get(i).getCustomerId());
            assertEquals("last-" + (i + 1), largeData.get(i).getCustomerLastName());
            assertEquals("first-" + (i + 1), largeData.get(i).getCustomerFirstName());
            assertEquals("comment-" + (i + 1), largeData.get(i).getCustomerComment());
        }
    }
}
