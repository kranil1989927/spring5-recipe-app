package learn.springframework.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import learn.springframework.commands.UnitOfMeasureCommand;
import learn.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import learn.springframework.domain.UnitOfMeasure;
import learn.springframework.repositories.UnitOfMeasureRepository;

public class UnitOfMeasureServiceImplTest {

	UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand;
	UnitOfMeasureService unitOfMeasureService;

	@Mock
	UnitOfMeasureRepository unitOfMeasureRepository;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		unitOfMeasureToUnitOfMeasureCommand = new UnitOfMeasureToUnitOfMeasureCommand();
		unitOfMeasureService = new UnitOfMeasureServiceImpl(unitOfMeasureRepository, unitOfMeasureToUnitOfMeasureCommand);
	}

	@Test
    public void listAllUoms() throws Exception {
		Set<UnitOfMeasure> unitOfMeasures = new HashSet<>();
		
		UnitOfMeasure uom1 = new UnitOfMeasure();
		uom1.setId(1L);
		unitOfMeasures.add(uom1);
		
		UnitOfMeasure uom2 = new UnitOfMeasure();
		uom2.setId(2L);
		unitOfMeasures.add(uom2);
		
		when(unitOfMeasureRepository.findAll()).thenReturn(unitOfMeasures);
		
		Set<UnitOfMeasureCommand> commands = unitOfMeasureService.listAllUoms();
		
		assertEquals(2,commands.size());
		verify(unitOfMeasureRepository, times(1)).findAll();
	}

}
