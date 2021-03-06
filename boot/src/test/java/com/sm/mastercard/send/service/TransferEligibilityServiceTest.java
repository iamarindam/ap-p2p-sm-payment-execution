package com.sm.mastercard.send.service;

import com.sm.mastercard.send.constants.McSendConstants;
import com.sm.mastercard.send.integration.config.McSendIntegrationGateway;
import com.sm.mastercard.send.model.ErrorResponse;
import com.sm.mastercard.send.model.McTransferEligibilityResponse;
import com.sm.mastercard.send.model.SigningServiceResponse;
import com.sm.mastercard.send.model.TransferEligibilityRequest;
import com.sm.mastercard.send.util.McSendTestUtil;
import com.sm.mastercard.send.util.McSendUtil;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.isA;

@RunWith(MockitoJUnitRunner.class)
public class TransferEligibilityServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransferEligibilityServiceTest.class);

    @Mock
    private static TransferEligibilityRequest transferEligibilityRequest;

    @Mock
    private static McTransferEligibilityResponse transferEligibilityResponse;

    @Mock
    private static SigningServiceResponse signingServiceResponse;

    @Mock
    private static ErrorResponse transferEligibilityErrorResponse;

    @Mock
    private static McSendUtil mcSendUtil;

    @Mock
    private static McSendIntegrationGateway mcSendIntegrationGateway;

    @InjectMocks
    private static TransferEligibilityService transferEligibilityService = new TransferEligibilityService();

    @InjectMocks
    private McSendTestUtil mcSendTestUtil = new McSendTestUtil();

    private static String participantId;
    private static String partnerId;
    private static String correlationId;
    private static String businessMsgIdentifier;
    private static String signature;

    @BeforeClass
    public static void setUp() {
        transferEligibilityRequest = Mockito.mock(TransferEligibilityRequest.class);
        transferEligibilityResponse = Mockito.mock(McTransferEligibilityResponse.class);
        transferEligibilityErrorResponse = Mockito.mock(ErrorResponse.class);
        signingServiceResponse = Mockito.mock(SigningServiceResponse.class);
        mcSendIntegrationGateway = Mockito.mock(McSendIntegrationGateway.class);
        mcSendUtil = Mockito.mock(McSendUtil.class);
        participantId = McSendTestUtil.getParticipantId();
        partnerId = McSendTestUtil.getPartnerId();
        correlationId = McSendTestUtil.getCorrelationId();
        businessMsgIdentifier = McSendTestUtil.getBusinessMsgIdentifier();
        signature = McSendTestUtil.getSignature();
        ReflectionTestUtils.setField(transferEligibilityService, "source", "ZAPP");
        ReflectionTestUtils.setField(transferEligibilityService, "isSigningEnabled", false);
        ReflectionTestUtils.setField(transferEligibilityService, "partnerId", partnerId);
    }

    @Test
    public void test_initiateTransferEligibility_success_200() throws IOException, URISyntaxException {
        LOGGER.info("Came into method test_initiateTransferEligibility() success.. ");
        boolean encrypted = false;
        ReflectionTestUtils.setField(transferEligibilityService, "isSigningEnabled", false);
        //Building input request
        URL request = SendHandlerTest.class.getResource(mcSendTestUtil.getOiToSmRequest_te());
        File requestFile = Paths.get(request.toURI()).toFile();
        transferEligibilityRequest = McSendTestUtil.objectMapper().readValue(requestFile, TransferEligibilityRequest.class);

        //Building output response
        URL response = SendHandlerTest.class.getResource(mcSendTestUtil.getSmToOiResponse_te());
        File responseFile = Paths.get(response.toURI()).toFile();
        transferEligibilityResponse = McSendTestUtil.objectMapper().readValue(responseFile, McTransferEligibilityResponse.class);

        Message<?> smToMcMessage = MessageBuilder.withPayload(transferEligibilityResponse)
                .setHeader(McSendConstants.X_PARTICIPANT_ID, participantId)
                .setHeader(McSendConstants.HTTP_STATUSCODE,HttpStatus.OK).build();

        MessageBuilder build = MessageBuilder.fromMessage(smToMcMessage);

        //when
        Mockito.when(mcSendIntegrationGateway.eligibility(
                isA(TransferEligibilityRequest.class),//transferEligibilityRequest,
                isA(String.class),//correlationId,
                isA(String.class),//participantId,
                isA(String.class),//partnerId,
                isA(Boolean.class)))//enableEncryption))
                .thenReturn(build.build()); //then

        //actual
        ResponseEntity<Object> actualResponse =  transferEligibilityService.
                initiateTransferEligibility(transferEligibilityRequest,correlationId,
                        participantId,businessMsgIdentifier,signature,encrypted);

        Assertions.assertThat(actualResponse.getBody())
                .isEqualToComparingFieldByFieldRecursively(transferEligibilityResponse);

        LOGGER.info("Exiting method test_initiateTransferEligibility() success.. ");
    }

    @Test
    public void test_initiateTransferEligibility_success_201() throws IOException, URISyntaxException {
        LOGGER.info("Came into method test_initiateTransferEligibility() success.. ");
        boolean encrypted = false;
        ReflectionTestUtils.setField(transferEligibilityService, "isSigningEnabled", false);
        //Building input request
        URL request = SendHandlerTest.class.getResource(mcSendTestUtil.getOiToSmRequest_te());
        File requestFile = Paths.get(request.toURI()).toFile();
        transferEligibilityRequest = McSendTestUtil.objectMapper().readValue(requestFile, TransferEligibilityRequest.class);

        //Building output response
        URL response = SendHandlerTest.class.getResource(mcSendTestUtil.getSmToOiResponse_te());
        File responseFile = Paths.get(response.toURI()).toFile();
        transferEligibilityResponse = McSendTestUtil.objectMapper().readValue(responseFile, McTransferEligibilityResponse.class);

        Message<?> smToMcMessage = MessageBuilder.withPayload(transferEligibilityResponse)
                .setHeader(McSendConstants.X_PARTICIPANT_ID, participantId)
                .setHeader(McSendConstants.HTTP_STATUSCODE,HttpStatus.CREATED).build();

        MessageBuilder build = MessageBuilder.fromMessage(smToMcMessage);

        //when
        Mockito.when(mcSendIntegrationGateway.eligibility(
                isA(TransferEligibilityRequest.class),//transferEligibilityRequest,
                isA(String.class),//correlationId,
                isA(String.class),//participantId,
                isA(String.class),//partnerId,
                isA(Boolean.class)))//enableEncryption))
                .thenReturn(build.build()); //then

        //actual
        ResponseEntity<Object> actualResponse =  transferEligibilityService.
                initiateTransferEligibility(transferEligibilityRequest,correlationId,
                        participantId,businessMsgIdentifier,signature,encrypted);

        Assertions.assertThat(actualResponse.getBody())
                .isEqualToComparingFieldByFieldRecursively(transferEligibilityResponse);

        LOGGER.info("Exiting method test_initiateTransferEligibility() success.. ");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void test_initiateTransferEligibility_failure() throws IOException, URISyntaxException {
        LOGGER.info("Came into method test_initiateTransferEligibility() failure.. ");
        boolean encrypted = false;
        ReflectionTestUtils.setField(transferEligibilityService, "isSigningEnabled", false);
        //Building input request
        URL request = SendHandlerTest.class.getResource(mcSendTestUtil.getOiToSmRequest_te());
        File requestFile = Paths.get(request.toURI()).toFile();
        transferEligibilityRequest = McSendTestUtil.objectMapper().readValue(requestFile, TransferEligibilityRequest.class);
        transferEligibilityRequest.setRecipientAccountUri("951234002");
        //Building output response
        URL response = SendHandlerTest.class.getResource(mcSendTestUtil.getError_response());
        File responseFile = Paths.get(response.toURI()).toFile();
        transferEligibilityErrorResponse = McSendTestUtil.objectMapper().readValue(responseFile, ErrorResponse.class);

        ResponseEntity<Object> errRes = new ResponseEntity<>(transferEligibilityErrorResponse,HttpStatus.NOT_ACCEPTABLE);
        Message<?> smToMcMessage = MessageBuilder.withPayload(transferEligibilityErrorResponse)
                .setHeader(McSendConstants.X_PARTICIPANT_ID, participantId)
                .setHeader(McSendConstants.HTTP_STATUSCODE,HttpStatus.BAD_REQUEST).build();

        MessageBuilder build = MessageBuilder.fromMessage(smToMcMessage);

        //when
        Mockito.when(mcSendIntegrationGateway.eligibility(
                isA(TransferEligibilityRequest.class),//transferEligibilityRequest,
                isA(String.class),//correlationId,
                isA(String.class),//participantId,
                isA(String.class),//partnerId,
                isA(Boolean.class)))//enableEncryption))
                .thenReturn(build.build()); //then

        Mockito.when(mcSendUtil.generateErrorResponseToAHI(HttpStatus.BAD_REQUEST,
                smToMcMessage)).thenReturn(errRes);

        //actual
        ResponseEntity<Object> actualResponse =  transferEligibilityService.
                initiateTransferEligibility(transferEligibilityRequest,correlationId,
                        participantId,businessMsgIdentifier,signature,encrypted);

        Assertions.assertThat(actualResponse.getBody())
                .isEqualToComparingFieldByFieldRecursively(transferEligibilityErrorResponse);

        LOGGER.info("Exiting method test_initiateTransferEligibility() failure.. ");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void test_SignEnabled_SMToOI() throws IOException, URISyntaxException{
        LOGGER.info("Came into method test_SignEnabled_SMToOI() .. ");
        boolean encrypted = false;
        ReflectionTestUtils.setField(transferEligibilityService, "isSigningEnabled", true);

        //Building input request
        URL request = SendHandlerTest.class.getResource(mcSendTestUtil.getOiToSmRequest_te());
        File requestFile = Paths.get(request.toURI()).toFile();
        transferEligibilityRequest = McSendTestUtil.objectMapper().readValue(requestFile, TransferEligibilityRequest.class);

        //Building output response
        URL response = SendHandlerTest.class.getResource(mcSendTestUtil.getSmToOiResponse_te());
        File responseFile = Paths.get(response.toURI()).toFile();
        transferEligibilityResponse = McSendTestUtil.objectMapper().readValue(responseFile, McTransferEligibilityResponse.class);

        Message<?> signatureVerificationRes = MessageBuilder.withPayload(signingServiceResponse)
                .setHeader(McSendConstants.X_PARTICIPANT_ID, participantId)
                .setHeader(McSendConstants.HTTP_STATUSCODE,HttpStatus.OK).build();
        MessageBuilder build = MessageBuilder.fromMessage(signatureVerificationRes);
        //when
        Mockito.when(mcSendIntegrationGateway.transferEligibilitySignatureVerification(
                isA(Message.class)))
                .thenReturn(build.build()); //then

        Message<?> smToMcMessage = MessageBuilder.withPayload(transferEligibilityResponse)
                .setHeader(McSendConstants.X_PARTICIPANT_ID, participantId)
                .setHeader(McSendConstants.HTTP_STATUSCODE,HttpStatus.CREATED).build();
        MessageBuilder build1 = MessageBuilder.fromMessage(smToMcMessage);
        //when
        Mockito.when(mcSendIntegrationGateway.eligibility(
                isA(TransferEligibilityRequest.class),//transferEligibilityRequest,
                isA(String.class),//correlationId,
                isA(String.class),//participantId,
                isA(String.class),//partnerId,
                isA(Boolean.class)))//enableEncryption))
                .thenReturn(build1.build()); //then

        Message<?> signatureGenRes = MessageBuilder.withPayload(transferEligibilityResponse)
                .setHeader(McSendConstants.HTTP_STATUSCODE,HttpStatus.OK)
                .setHeader(McSendConstants.X_JWS_SIGNATURE,signature).build();
        MessageBuilder build2 = MessageBuilder.fromMessage(signatureGenRes);
        //when
        Mockito.when(mcSendIntegrationGateway.transferEligibilitySigningGeneration(
                isA(Message.class)))
                .thenReturn(build2.build()); //then

        //actual
        ResponseEntity<Object> actualResponse =  transferEligibilityService.
                initiateTransferEligibility(transferEligibilityRequest,correlationId,
                        participantId,businessMsgIdentifier,signature,encrypted);

        Assertions.assertThat(actualResponse.getBody())
                .isEqualToComparingFieldByFieldRecursively(transferEligibilityResponse);

        LOGGER.info("Exiting method test_SignEnabled_SMToOI() .. ");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void test_SignEnabled_verification_failure() throws IOException, URISyntaxException{
        LOGGER.info("Came into method test_SignEnabled_verification() .. ");
        boolean encrypted = false;
        ReflectionTestUtils.setField(transferEligibilityService, "isSigningEnabled", true);

        //Building input request
        URL request = SendHandlerTest.class.getResource(mcSendTestUtil.getOiToSmRequest_te());
        File requestFile = Paths.get(request.toURI()).toFile();
        transferEligibilityRequest = McSendTestUtil.objectMapper().readValue(requestFile, TransferEligibilityRequest.class);

        //Building output response
        URL response = SendHandlerTest.class.getResource(mcSendTestUtil.getError_response());
        File responseFile = Paths.get(response.toURI()).toFile();
        transferEligibilityErrorResponse = McSendTestUtil.objectMapper().readValue(responseFile, ErrorResponse.class);

        ResponseEntity<Object> errRes = new ResponseEntity<>(transferEligibilityErrorResponse,HttpStatus.NOT_ACCEPTABLE);

        Message<?> signatureVerificationRes = MessageBuilder.withPayload(signingServiceResponse)
                .setHeader(McSendConstants.HTTP_STATUSCODE,HttpStatus.BAD_REQUEST).build();
        MessageBuilder build = MessageBuilder.fromMessage(signatureVerificationRes);
        //when
        Mockito.when(mcSendIntegrationGateway.transferEligibilitySignatureVerification(
                isA(Message.class)))
                .thenReturn(build.build()); //then

        Mockito.when(mcSendUtil.generateErrorResponseToAHI(HttpStatus.BAD_REQUEST,
                signatureVerificationRes)).thenReturn(errRes);

        //actual
        ResponseEntity<Object> actualResponse =  transferEligibilityService.
                initiateTransferEligibility(transferEligibilityRequest,correlationId,
                        participantId,businessMsgIdentifier,signature,encrypted);

        Assertions.assertThat(actualResponse.getBody())
                .isEqualToComparingFieldByFieldRecursively(transferEligibilityErrorResponse);

        LOGGER.info("Exiting method test_SignEnabled_verification() .. ");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void test_SignEnabled_generation_failure() throws IOException, URISyntaxException{
        LOGGER.info("Came into method test_SignEnabled_generation() .. ");
        boolean encrypted = false;
        ReflectionTestUtils.setField(transferEligibilityService, "isSigningEnabled", true);

        //Building input request
        URL request = SendHandlerTest.class.getResource(mcSendTestUtil.getOiToSmRequest_te());
        File requestFile = Paths.get(request.toURI()).toFile();
        transferEligibilityRequest = McSendTestUtil.objectMapper().readValue(requestFile, TransferEligibilityRequest.class);

        //Building output response
        URL response = SendHandlerTest.class.getResource(mcSendTestUtil.getSmToOiResponse_te());
        File responseFile = Paths.get(response.toURI()).toFile();
        transferEligibilityResponse = McSendTestUtil.objectMapper().readValue(responseFile, McTransferEligibilityResponse.class);

        //Building output response
        URL errResponse = SendHandlerTest.class.getResource(mcSendTestUtil.getError_response());
        File errResponseFile = Paths.get(errResponse.toURI()).toFile();
        transferEligibilityErrorResponse = McSendTestUtil.objectMapper().readValue(errResponseFile, ErrorResponse.class);
        ResponseEntity<Object> errRes = new ResponseEntity<>(transferEligibilityErrorResponse,HttpStatus.NOT_ACCEPTABLE);

        Message<?> signatureVerificationRes = MessageBuilder.withPayload(signingServiceResponse)
                .setHeader(McSendConstants.HTTP_STATUSCODE,HttpStatus.OK).build();
        MessageBuilder build = MessageBuilder.fromMessage(signatureVerificationRes);
        //when
        Mockito.when(mcSendIntegrationGateway.transferEligibilitySignatureVerification(
                isA(Message.class)))
                .thenReturn(build.build()); //then

        Message<?> smToMcMessage = MessageBuilder.withPayload(transferEligibilityResponse)
                .setHeader(McSendConstants.HTTP_STATUSCODE,HttpStatus.CREATED).build();
        MessageBuilder build1 = MessageBuilder.fromMessage(smToMcMessage);
        //when
        Mockito.when(mcSendIntegrationGateway.eligibility(
                isA(TransferEligibilityRequest.class),//transferEligibilityRequest,
                isA(String.class),//correlationId,
                isA(String.class),//participantId,
                isA(String.class),//partnerId,
                isA(Boolean.class)))//enableEncryption))
                .thenReturn(build1.build()); //then

        Message<?> signatureGenRes = MessageBuilder.withPayload(transferEligibilityResponse)
                .setHeader(McSendConstants.HTTP_STATUSCODE,HttpStatus.BAD_REQUEST).build();
        MessageBuilder build2 = MessageBuilder.fromMessage(signatureGenRes);
        //when
        Mockito.when(mcSendIntegrationGateway.transferEligibilitySigningGeneration(
                isA(Message.class)))
                .thenReturn(build2.build()); //then

        Mockito.when(mcSendUtil.generateErrorResponseToAHI(HttpStatus.BAD_REQUEST,
                signatureGenRes)).thenReturn(errRes);

        //actual
        ResponseEntity<Object> actualResponse =  transferEligibilityService.
                initiateTransferEligibility(transferEligibilityRequest,correlationId,
                        participantId,businessMsgIdentifier,signature,encrypted);

        Assertions.assertThat(actualResponse.getBody())
                .isEqualToComparingFieldByFieldRecursively(transferEligibilityErrorResponse);

        LOGGER.info("Exiting method test_SignEnabled_generation() .. ");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void test_generateToken_success() throws IOException, URISyntaxException{
        LOGGER.info("Came into method test_generateToken_success() .. ");
        //Building input request
        URL request = SendHandlerTest.class.getResource(mcSendTestUtil.getOiToSmRequest_te());
        File requestFile = Paths.get(request.toURI()).toFile();
        transferEligibilityRequest = McSendTestUtil.objectMapper().readValue(requestFile, TransferEligibilityRequest.class);

        Message<?> msgGenRes = MessageBuilder.withPayload(transferEligibilityRequest)
                .setHeader(McSendConstants.X_JWS_SIGNATURE,signature)
                .setHeader(McSendConstants.HTTP_STATUSCODE, HttpStatus.OK).build();
        MessageBuilder build = MessageBuilder.fromMessage(msgGenRes);

        //when
        Mockito.when(mcSendIntegrationGateway.testSignatureGeneration(
                isA(Message.class)))
                .thenReturn(build.build()); //then

        //actual
        ResponseEntity<String> actualResponse =  transferEligibilityService.
                generateJwsToken(transferEligibilityRequest);

        Assertions.assertThat(actualResponse.getBody())
                .isSameAs(signature);

        LOGGER.info("Exiting method test_generateToken_success() .. ");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void test_generateToken_failure() throws IOException, URISyntaxException{

        LOGGER.info("Came into method test_generateToken_failure() .. ");
        //Building input request
        URL request = SendHandlerTest.class.getResource(mcSendTestUtil.getOiToSmRequest_te());
        File requestFile = Paths.get(request.toURI()).toFile();
        transferEligibilityRequest = McSendTestUtil.objectMapper().readValue(requestFile, TransferEligibilityRequest.class);

        Message<?> msgGenRes = MessageBuilder.withPayload(transferEligibilityRequest)
                .setHeader(McSendConstants.HTTP_STATUSCODE, HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        MessageBuilder build = MessageBuilder.fromMessage(msgGenRes);

        //when
        Mockito.when(mcSendIntegrationGateway.testSignatureGeneration(
                isA(Message.class)))
                .thenReturn(build.build()); //then

        //actual
        ResponseEntity<String> actualResponse =  transferEligibilityService.
                generateJwsToken(transferEligibilityRequest);

        Assertions.assertThat(actualResponse.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        LOGGER.info("Exiting method test_generateToken_failure() .. ");
    }

    @AfterClass
    public static void tearDown() {
        System.out.println(TransferEligibilityServiceTest.class+" has been tested and code coverage checked .. ");
    }
}
