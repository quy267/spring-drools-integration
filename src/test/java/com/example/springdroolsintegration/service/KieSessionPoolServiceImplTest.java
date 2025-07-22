package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.config.DroolsProperties;
import com.example.springdroolsintegration.service.impl.KieSessionPoolServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.AgendaGroup;
import org.kie.api.runtime.rule.Agenda;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KieSessionPoolServiceImplTest {

    @Mock
    private KieBase kieBase;

    @Mock
    private DroolsProperties droolsProperties;

    @Mock
    private KieSession kieSession;

    @Mock
    private Agenda agenda;

    private KieSessionPoolServiceImpl kieSessionPoolService;

    @BeforeEach
    void setUp() {
        // Configure mocks
        when(droolsProperties.getSessionPoolSize()).thenReturn(5);
        when(droolsProperties.getKieSessionName()).thenReturn("testSession");
        when(kieBase.newKieSession()).thenReturn(kieSession);
        when(kieSession.getAgenda()).thenReturn(agenda);
        
        // Mock the getFactHandles method to return a collection of fact handles
        Collection<FactHandle> factHandles = new ArrayList<>();
        when(kieSession.getFactHandles()).thenReturn(factHandles);
        
        // Create the service instance
        kieSessionPoolService = new KieSessionPoolServiceImpl(kieBase, droolsProperties);
    }

    @Test
    void testBorrowSession() {
        // Act
        KieSession session = kieSessionPoolService.borrowSession();
        
        // Assert
        assertNotNull(session);
        assertEquals(kieSession, session);
        assertEquals(1, kieSessionPoolService.getTotalSessionsCreated());
        assertEquals(1, kieSessionPoolService.getTotalSessionsBorrowed());
        
        // Verify that a new session was created
        verify(kieBase).newKieSession();
    }

    @Test
    void testBorrowSessionWithName() {
        // Act
        KieSession session = kieSessionPoolService.borrowSession("customSession");
        
        // Assert
        assertNotNull(session);
        assertEquals(kieSession, session);
        assertEquals(1, kieSessionPoolService.getTotalSessionsCreated());
        assertEquals(1, kieSessionPoolService.getTotalSessionsBorrowed());
        
        // Verify that a new session was created
        verify(kieBase).newKieSession();
    }

    @Test
    void testReturnSession() {
        // Arrange
        KieSession session = kieSessionPoolService.borrowSession();
        
        // Act
        kieSessionPoolService.returnSession(session);
        
        // Assert
        assertEquals(1, kieSessionPoolService.getPoolSize());
        assertEquals(1, kieSessionPoolService.getTotalSessionsReturned());
        
        // Verify that the session was reset
        verify(kieSession).getFactHandles();
        verify(agenda).clear();
    }

    @Test
    void testReturnSessionWhenPoolIsFull() {
        // Arrange
        when(droolsProperties.getSessionPoolSize()).thenReturn(1);
        
        // Borrow and return a session to fill the pool
        KieSession session1 = kieSessionPoolService.borrowSession();
        kieSessionPoolService.returnSession(session1);
        
        // Borrow and return another session when the pool is full
        KieSession session2 = kieSessionPoolService.borrowSession();
        
        // Act
        kieSessionPoolService.returnSession(session2);
        
        // Assert
        assertEquals(1, kieSessionPoolService.getPoolSize());
        assertEquals(2, kieSessionPoolService.getTotalSessionsReturned());
        
        // Verify that the second session was disposed
        verify(kieSession).dispose();
    }

    @Test
    void testReturnNullSession() {
        // Act
        kieSessionPoolService.returnSession(null);
        
        // Assert
        assertEquals(0, kieSessionPoolService.getPoolSize());
        assertEquals(0, kieSessionPoolService.getTotalSessionsReturned());
    }

    @Test
    void testClearPool() {
        // Arrange
        KieSession session1 = kieSessionPoolService.borrowSession();
        KieSession session2 = kieSessionPoolService.borrowSession();
        kieSessionPoolService.returnSession(session1);
        kieSessionPoolService.returnSession(session2);
        
        // Act
        kieSessionPoolService.clearPool();
        
        // Assert
        assertEquals(0, kieSessionPoolService.getPoolSize());
        
        // Verify that sessions were disposed
        verify(kieSession, times(2)).dispose();
    }

    @Test
    void testGetPoolSize() {
        // Arrange
        KieSession session1 = kieSessionPoolService.borrowSession();
        KieSession session2 = kieSessionPoolService.borrowSession();
        kieSessionPoolService.returnSession(session1);
        
        // Act
        int poolSize = kieSessionPoolService.getPoolSize();
        
        // Assert
        assertEquals(1, poolSize);
    }

    @Test
    void testGetTotalSessionsCreated() {
        // Arrange
        kieSessionPoolService.borrowSession();
        kieSessionPoolService.borrowSession();
        
        // Act
        long totalCreated = kieSessionPoolService.getTotalSessionsCreated();
        
        // Assert
        assertEquals(2, totalCreated);
    }

    @Test
    void testGetTotalSessionsBorrowed() {
        // Arrange
        kieSessionPoolService.borrowSession();
        kieSessionPoolService.borrowSession("customSession");
        
        // Act
        long totalBorrowed = kieSessionPoolService.getTotalSessionsBorrowed();
        
        // Assert
        assertEquals(2, totalBorrowed);
    }

    @Test
    void testGetTotalSessionsReturned() {
        // Arrange
        KieSession session1 = kieSessionPoolService.borrowSession();
        KieSession session2 = kieSessionPoolService.borrowSession();
        kieSessionPoolService.returnSession(session1);
        
        // Act
        long totalReturned = kieSessionPoolService.getTotalSessionsReturned();
        
        // Assert
        assertEquals(1, totalReturned);
    }

    @Test
    void testExceptionHandlingWhenResettingSession() {
        // Arrange
        when(kieSession.getFactHandles()).thenThrow(new RuntimeException("Test exception"));
        KieSession session = kieSessionPoolService.borrowSession();
        
        // Act
        kieSessionPoolService.returnSession(session);
        
        // Assert
        assertEquals(0, kieSessionPoolService.getPoolSize());
        
        // Verify that the session was disposed
        verify(kieSession).dispose();
    }

    @Test
    void testExceptionHandlingWhenDisposingSession() {
        // Arrange
        when(kieSession.getFactHandles()).thenThrow(new RuntimeException("Test exception"));
        doThrow(new RuntimeException("Dispose exception")).when(kieSession).dispose();
        KieSession session = kieSessionPoolService.borrowSession();
        
        // Act
        kieSessionPoolService.returnSession(session);
        
        // Assert
        assertEquals(0, kieSessionPoolService.getPoolSize());
        
        // Verify that dispose was called
        verify(kieSession).dispose();
    }
}