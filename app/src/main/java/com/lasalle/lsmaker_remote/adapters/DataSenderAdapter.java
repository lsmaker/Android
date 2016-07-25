package com.lasalle.lsmaker_remote.adapters;


import java.nio.ByteBuffer;

/**
 * DataSenderAdapter is an adapter used to generate the data to send to the device using a known
 * common API and format.
 *
 * @author Eduard de Torres
 * @version 1.0.0
 */
public class DataSenderAdapter {

    // Bit masks
    private static final byte TCP_PROTOCOL_MASK = 0b1000000;
    private static final byte ACK_MASK = 0b01000000;
    private static final byte SYS_USR_MASK = 0b00000001;

    // OPCODES
    private static final byte MOVEMENT_OPCODE = 0x00;
    private static final byte ASK_FOR_SPEED_OPCODE = 0x02;
    private static final byte RETURN_OF_ASK_FOR_SPEED_OPCODE = 0x03;
    private static final byte ASK_FOR_POSITION_OPCODE = 0x04;
    private static final byte RETURN_OF_ASK_FOR_POSITION_OPCODE = 0x05;
    private static final byte ASK_FOR_ORIENTATION_OPCODE = 0x06;
    private static final byte RETURN_OF_ASK_FOR_ORIENTATION_OPCODE = 0x07;
    private static final byte SEND_TEXT_OPCODE = 0x08;


    /**
     * Method that generate a movement frame ready to be sent to the LsMaker device.
     *
     * This method receives a set of parameters to configure the frame, such as the desired
     * device speed, acceleration and turn.
     *
     * @param speed percentage value inside rang [-100, 100] to select the desired speed
     * @param acceleration percentage value inside rang [-100, 100] to select the desired acceleration
     * @param turn percentage value inside rang [-100, 100] to select the desired turn
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateMovementFrame (int speed, int acceleration, int turn) {
        // First of all, we must ensure that the given data is between the correct bounds.
        correctData(speed, -100, 100);
        correctData(acceleration, -100, 100);
        correctData(turn, -100, 100);

        // 2 bytes for the header + OPCODE and 3 integers as payload (only its lower byte).
        int capacity = 2 + 3;
        // Creates a buffer to prepare data to generate the final byte array.
        ByteBuffer buffer = ByteBuffer.allocate(capacity);

        // Puts the header to the buffer.
        buffer.put(generateHeader(false, false, false));
        // Puts the OPCODE to the buffer.
        buffer.put(MOVEMENT_OPCODE);
        // Puts the payload to the buffer.
        // Gets only the lower byte of an int.
        buffer.put((byte) speed);
        buffer.put((byte) acceleration);
        buffer.put((byte) turn);

        byte[] frame = buffer.array();
        return frame;
    }

    /**
     * Method that generate a movement frame ready to be sent to the LsMaker device that will
     * ask for an acknowledge frame from the device.
     *
     * This method receives a set of parameters to configure the frame, such as the desired
     * device speed, acceleration and turn.
     *
     * @param speed percentage value inside rang [-100, 100] to select the desired speed
     * @param acceleration percentage value inside rang [-100, 100] to select the desired acceleration
     * @param turn percentage value inside rang [-100, 100] to select the desired turn
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateMovementFrameWithAck (int speed, int acceleration, int turn) {
        byte[] frame = generateMovementFrame(speed, acceleration, turn);
        frame[0] = generateHeader(true, false, false);
        return frame;
    }

    /**
     * Method that generates an 'ask for speed' frame ready to be sent to the LsMaker device.
     *
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateAskForSpeedFrame () {
        // 2 bytes for the header.
        int capacity = 2;
        // Creates a buffer to prepare data to generate the final byte array.
        ByteBuffer buffer = ByteBuffer.allocate(capacity);

        // Puts the header to the buffer.
        buffer.put(generateHeader(false, false, false));
        // Puts the OPCODE to the buffer.
        buffer.put(ASK_FOR_SPEED_OPCODE);

        byte[] frame = buffer.array();
        return frame;
    }

    /**
     * Method that generates an 'ask for speed' frame ready to be sent to the LsMaker device that
     * will ask for an acknowledge from the device.
     *
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateAskForSpeedFrameWithAck () {
        byte[] frame = generateAskForSpeedFrame();
        frame[0] = generateHeader(true, false, false);
        return frame;
    }

    /**
     * Method that generates a frame returning the information asked by an 'ask for speed' frame.
     *
     * @param speed value to send to the device as current speed
     *
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateReturnAskForSpeedFrame (float speed) {
        // 2 bytes for the header + OPCODE and 1 float as payload.
        int capacity = 2 + Float.SIZE / Byte.SIZE;
        // Creates a buffer to prepare data to generate the final byte array.
        ByteBuffer buffer = ByteBuffer.allocate(capacity);

        // Puts the header to the buffer.
        buffer.put(generateHeader(false, false, false));
        // Puts the OPCODE to the buffer.
        buffer.put(RETURN_OF_ASK_FOR_SPEED_OPCODE);
        // Puts the payload to the buffer.
        // Gets only the lower byte of an int.
        buffer.putFloat(speed);

        byte[] frame = buffer.array();
        return frame;
    }

    /**
     * Method that generates a frame returning the information asked by an 'ask for speed' frame
     * that will ask for an acknowledge from the device.
     *
     * @param speed value to send to the device as current speed
     *
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateReturnAskForSpeedFrameWithAck (float speed) {
        byte[] frame = generateReturnAskForSpeedFrame(speed);
        frame[0] = generateHeader(true, false, false);
        return frame;
    }

    /**
     * Method that generates an 'ask for position' frame ready to be sent to the LsMaker device.
     *
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateAskForPositionFrame () {
        // 2 bytes for the header.
        int capacity = 2;
        // Creates a buffer to prepare data to generate the final byte array.
        ByteBuffer buffer = ByteBuffer.allocate(capacity);

        // Puts the header to the buffer.
        buffer.put(generateHeader(false, false, false));
        // Puts the OPCODE to the buffer.
        buffer.put(ASK_FOR_POSITION_OPCODE);

        byte[] frame = buffer.array();
        return frame;
    }

    /**
     * Method that generates an 'ask for position' frame ready to be sent to the LsMaker device
     * that will ask for an acknowledge from the device.
     *
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateAskForPositionrameWithAck () {
        byte[] frame = generateAskForPositionFrame();
        frame[0] = generateHeader(true, false, false);
        return frame;
    }


    /**
     * Method that generates a frame returning the information asked by an 'ask for position' frame.
     *
     * @param x angle to send as current x
     * @param y angle to send as current y
     * @param z angle to send as current z
     *
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateReturnAskForPositionFrame (short x, short y, short z) {
        // First of all, we must ensure that the given data is between the correct bounds.
        correctData(x, 0, 360);
        correctData(y, 0, 360);
        correctData(z, 0, 360);

        // 2 bytes for the header + OPCODE and 3 integers (4 bytes each) as payload.
        int capacity = 2 + 3 * Integer.SIZE / Byte.SIZE;
        // Creates a buffer to prepare data to generate the final byte array.
        ByteBuffer buffer = ByteBuffer.allocate(capacity);

        // Puts the header to the buffer.
        buffer.put(generateHeader(false, false, false));
        // Puts the OPCODE to the buffer.
        buffer.put(RETURN_OF_ASK_FOR_POSITION_OPCODE);
        // Puts the payload to the buffer.
        buffer.putShort(x);
        buffer.putShort(y);
        buffer.putShort(z);

        byte[] frame = buffer.array();
        return frame;
    }

    /**
     * Method that generates a frame returning the information asked by an 'ask for position' frame
     * that will ask for an acknowledge from the device.
     *
     * @param x angle to send as current x
     * @param y angle to send as current y
     * @param z angle to send as current z
     *
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateReturnAskForPositionFrameWithAck (short x, short y, short z) {
        byte[] frame = generateReturnAskForPositionFrame(x, y, z);
        frame[0] = generateHeader(true, false, false);
        return frame;
    }


    /**
     * Method that generates an 'ask for orientation' frame ready to be sent to the LsMaker device.
     *
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateAskForOrientationFrame () {
        // 2 bytes for the header.
        int capacity = 2;
        // Creates a buffer to prepare data to generate the final byte array.
        ByteBuffer buffer = ByteBuffer.allocate(capacity);

        // Puts the header to the buffer.
        buffer.put(generateHeader(false, false, false));
        // Puts the OPCODE to the buffer.
        buffer.put(ASK_FOR_ORIENTATION_OPCODE);

        byte[] frame = buffer.array();
        return frame;
    }

    /**
     * Method that generates an 'ask for orientation' frame ready to be sent to the LsMaker device
     * that will ask for an acknowledge from the device.
     *
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateAskForOrientationFrameWithAck () {
        byte[] frame = generateAskForOrientationFrame();
        frame[0] = generateHeader(true, false, false);
        return frame;
    }


    /**
     * Method that generates a frame returning the information asked by an 'ask for orientation'
     * frame.
     *
     * @param degrees value to send to the device as degrees from north
     *
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateReturnAskForOrientationFrame (short degrees) {
        // First of all, we must ensure that the given data is between the correct bounds.
        correctData(degrees, 0, 360);

        // 2 bytes for the header + OPCODE and 1 integer (4 bytes) as payload.
        int capacity = 2 + Integer.SIZE / Byte.SIZE;
        // Creates a buffer to prepare data to generate the final byte array.
        ByteBuffer buffer = ByteBuffer.allocate(capacity);

        // Puts the header to the buffer.
        buffer.put(generateHeader(false, false, false));
        // Puts the OPCODE to the buffer.
        buffer.put(RETURN_OF_ASK_FOR_ORIENTATION_OPCODE);
        // Puts the payload to the buffer.
        buffer.putShort(degrees);

        byte[] frame = buffer.array();
        return frame;
    }

    /**
     * Method that generates a frame returning the information asked by an 'ask for orientation'
     * frame
     * that will ask for an acknowledge from the device.
     *
     * @param degrees value to send to the device as degrees from north
     *
     * @return a frame to be sent to the device as a byte array
     */
    public byte[] generateReturnAskForOrientationFrameWithAck (short degrees) {
        byte[] frame = generateReturnAskForOrientationFrame(degrees);
        frame[0] = generateHeader(true, false, false);
        return frame;
    }

    /**
     * Method that corrects the given value to ensure that it will be inside a given bounds.
     *
     * @param data data to be corrected
     * @param minValue minimum value from bounds
     * @param maxValue maximum value from bounds
     *
     * @return the corrected data
     */
    private int correctData (int data, int minValue, int maxValue) {
        if (data < minValue) {
            data = minValue;
        } else if (data > maxValue) {
            data = maxValue;
        }
        return data;
    }

    /**
     * Method that creates a byte data to be used as header for the device's frames.
     *
     * This method receives a set of parameters to generate a standard header following the
     * format of the device's header's frame.
     *
     * @param protocol true if the frame wants to ask for an ack, false otherwise
     * @param ack true if the frame is an ack frame, false otherwise
     * @param sysUsr true if the frame uses an user OPCODE, false otherwise
     *
     * @return a byte with the header's information
     */
    private byte generateHeader (boolean protocol, boolean ack, boolean sysUsr) {
        byte header = 0;

        if (protocol) {
            header |= TCP_PROTOCOL_MASK;
        }
        if (ack) {
            header |= ACK_MASK;
        }
        if (sysUsr) {
            header |= SYS_USR_MASK;
        }

        return header;
    }
}
