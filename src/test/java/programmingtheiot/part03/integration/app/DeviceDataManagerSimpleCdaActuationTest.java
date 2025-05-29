/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

package programmingtheiot.part03.integration.app;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.gda.app.DeviceDataManager;
import programmingtheiot.gda.connection.IPubSubClient;
import programmingtheiot.gda.connection.MqttClientConnector;
import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;



/**
 * This test case class contains very basic integration tests for
 * DeviceDataManager. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class DeviceDataManagerSimpleCdaActuationTest
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(DeviceDataManagerSimpleCdaActuationTest.class.getName());
	

	// member var's
	
	
	// test setup methods
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}
	
	
	// test methods
	@Test
	public void testSendActuationEventsToCda() {
		
		ConfigUtil cfgUtil = ConfigUtil.getInstance();

		
		DeviceDataManager devDataMgr = new DeviceDataManager();
		
		devDataMgr.startManager();

		float nominalVal = cfgUtil.getFloat(
			ConfigConst.GATEWAY_DEVICE,
			ConfigConst.NOMINAL_HUMIDITY_SETTING);

		float lowVal = cfgUtil.getFloat(
			ConfigConst.GATEWAY_DEVICE,
			ConfigConst.TRIGGER_HUMIDIFIER_FLOOR);

		float highVal = cfgUtil.getFloat(
			ConfigConst.GATEWAY_DEVICE,
			ConfigConst.TRIGGER_HUMIDIFIER_CEILING);

		int delay = cfgUtil.getInteger(
			ConfigConst.GATEWAY_DEVICE,
			ConfigConst.HUMIDITY_MAX_TIME_PAST_THRESHOLD);

		generateAndProcessHumiditySensorDataSequence(devDataMgr, nominalVal, lowVal, highVal, delay);

		devDataMgr.stopManager();

	}

	private void generateAndProcessHumiditySensorDataSequence(DeviceDataManager ddm, float nominalVal, float lowVal, float highVal, int delay) {
		SensorData sd = new SensorData();
		sd.setName("My Test Humidity Sensor");
		sd.setLocationID("constraineddevice001");
		sd.setTypeID(ConfigConst.HUMIDITY_SENSOR_TYPE);

		// Enviar valores normales (no se espera actuación)
		sd.setValue(nominalVal);
		ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		waitForSeconds(2);

		sd.setValue(nominalVal);
		ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		waitForSeconds(2);

		// Enviar valor bajo fuera de rango por más de 10 segundos → actuar
		sd.setValue(lowVal - 2);
		ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		waitForSeconds(delay + 1);

		sd.setValue(lowVal - 1);
		ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		waitForSeconds(delay + 1);

		// Volver a nominal → desactivar
		sd.setValue(nominalVal);
		ddm.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sd);
		waitForSeconds(delay + 1);
	}

	private void waitForSeconds(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			// Ignorado
		}
	}



	
	
}
