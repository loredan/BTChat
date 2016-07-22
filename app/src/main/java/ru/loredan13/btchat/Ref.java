package ru.loredan13.btchat;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.util.Set;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: loredan
 * Date: 05.08.13
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
public class Ref
{
    public static UUID uuid = UUID.fromString("0b344d07-87a9-41cd-bc61-0e4e22f15f4d");
    public static BluetoothServerSocket btServerSocket;
    public static BluetoothSocket btSocket;
    public static final int INCOMING_MESSAGE = 1;
}
