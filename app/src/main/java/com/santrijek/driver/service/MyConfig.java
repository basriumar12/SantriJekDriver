package com.santrijek.driver.service;

/**
 * Created by GagahIB on 19/10/2016.
 */

public class MyConfig {

    public static String URL_GCM_SERVER = "https://fcm.googleapis.com/fcm/send";

    //public static String URL_SERVER = "http://santrijek.net/api/index.php/";
    public static String URL_SERVER = "http://35.224.48.185/santrijek/api/index.php/";

    public static String URL_TEST_SERVER = URL_SERVER + "book/";
    public static String URL_DRIVER_ACCEPT = URL_SERVER + "book/driver_accept_request";
    public static String URL_DRIVER_START = URL_SERVER + "book/driver_start_request";
    public static String URL_DRIVER_CANCEL = URL_SERVER + "book/driver_cancel_request";
    public static String URL_DRIVER_FINISH = URL_SERVER + "book/driver_finish_transaksi";
    public static String URL_DRIVER_FINISH_MMART = URL_SERVER + "book/driver_finish_transaksi_mstore";
    public static String URL_DRIVER_FINISH_MFOOD = URL_SERVER + "book/driver_finish_transaksi_mfood";
    public static String URL_DRIVER_REJECT = URL_SERVER + "book/driver_reject_transaksi";
    public static String URL_DRIVER_LOGIN = URL_SERVER + "driver/login";
    public static String URL_DRIVER_LOGOUT = URL_SERVER + "driver/logout";
    public static String UPDATE_LOCATION_URL = URL_SERVER + "driver/my_location";
    public static String URL_DRIVER_RATE_USER = URL_SERVER + "book/driver_rate_user";
    public static String URL_DRIVER_UPDATE_PROFILE = URL_SERVER + "driver/update_profile";
    public static String URL_DRIVER_UPDATE_REKENING = URL_SERVER + "driver/update_akun_bank";
    public static String URL_DRIVER_UPDATE_KENDARAAN = URL_SERVER + "driver/update_kendaraan";
    public static String URL_DRIVER_TURNING_ON = URL_SERVER + "driver/turning_on_bekerja";
    public static String URL_DRIVER_CHECK_VERSION = URL_SERVER + "driver/check_version";
    public static String URL_DRIVER_CHECK_STATUS_TRANSAKSI = URL_SERVER + "book/check_status_transaksi";
    public static String URL_DRIVER_GET_FEEDBACK = URL_SERVER + "driver/get_feedback";
    public static String URL_DRIVER_GET_RIWAYAT_TRANSAKSI = URL_SERVER + "driver/get_riwayat_transaksi";
    public static String URL_DRIVER_VERIVY_TOPUP = URL_SERVER + "driver/verifikasi_topup";
    public static String URL_DRIVER_WITHDRAWAL = URL_SERVER + "driver/withdrawal";
    public static String URL_DRIVER_SETTING_UANG_BELANJA = URL_SERVER + "driver/update_uang_belanja";
    public static String URL_DRIVER_SYNC_ACCOUNT = URL_SERVER + "driver/syncronizing_account";
    public static String URL_DRIVER_GET_TRANSAKSI_MMART = URL_SERVER + "book/get_data_transaksi_mmart";
    public static String URL_DRIVER_GET_TRANSAKSI_MFOOD = URL_SERVER + "book/get_data_transaksi_mfood";
    public static String URL_DRIVER_GET_TRANSAKSI_MBOX = URL_SERVER + "book/get_data_transaksi_mbox";
    public static String URL_DRIVER_GET_TRANSAKSI_MSEND = URL_SERVER + "book/get_data_transaksi_msend";
    public static String URL_DRIVER_GET_TRANSAKSI_MSERVICE = URL_SERVER + "book/get_data_transaksi_mservice";
    public static String URL_DRIVER_GET_TRANSAKSI_MMASSAGE = URL_SERVER + "book/get_data_transaksi_mmassage";
    public static String URL_DRIVER_GET_TRANSAKSI_MSTORE = URL_SERVER + "book/get_data_transaksi_mstore";
    public static String URL_DRIVER_GET_TRANSAKSI_MLAUNDRY = URL_SERVER + "book/get_data_transaksi_mlaundry";
    public static String URL_DRIVER_FINISH_MLAUNDRY = URL_SERVER + "book/driver_finish_transaksi_mlaundry";

    public static String GCM_KEY = "AIzaSyAHooAOLMfc9FlkDu5vXMVakeuj36W96BI";

    public static String USER_PREF = "USER_PREF ";
    public static String KENDARAAN_PREF = "KENDARAAN_PREF ";
    public static String SETTING_PREF = "SETTING_PREF ";
    public static String TAG_response = "RESPONSE ";
    public static String TAG_error = "ERROR ";

    public static String orderFragment = "ORDERFRAGMENT";
    public static String dashFragment = "DASHBOARDFRAGMENT";

    public static String orderType = "1";
    public static String chatType = "2";
    public static String locType = "3";

    public static int chatMe = 0;
    public static int chatYou = 1;

}