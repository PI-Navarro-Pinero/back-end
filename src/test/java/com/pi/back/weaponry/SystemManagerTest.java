package com.pi.back.weaponry;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemManagerTest {

    @Mock
    ProcessesManager processesManager;

    @InjectMocks
    SystemManager sut;

}