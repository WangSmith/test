package com.paulavasile.dineunite.Global;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.paulavasile.dineunite.data.FoundTableInfo;
import com.paulavasile.dineunite.data.TableBookingHost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silve on 5/20/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    public static int DATABASE_VERSION = 1;

    public static String DATABASE_NAME = "DineUnite";
    public static String SOCIAL_TABLE_NAME = "SocialTable";
    public static String RESTAURANT_TABLE_NAME = "RestaurantTable";
    public static String LOGIN_INFORMATION  = "LoginTable";
    public static String BOOKING_TABLE_NAME = "BookingTable";
    public static String PENDING_TABLE_NAME = "PendingTable";


    public static String RESTAURANT_ID_FIELD = "RestaurantID";
    public static String RESTAURANT_NAME_FIELD = "RestaurantName";
    public static String RESTAURANT_IMAGE_FIELD = "RestaurantImage";
    public static String RESTAURANT_DISTANCE_FIELD = "RestaurantDistance";
    public static String RESTAURANT_POSTCODE_FIELD = "RestaurantPostCode";
    public static String RESTAURANT_ADDRESS_FIELD = "RestaurantAddress";
    public static String RESTAURANT_LATITUDE_FIELD = "RestaurantLatitude";
    public static String RESTAURANT_LONGITUDE_FIELD = "RestaurantLongitude";
    public static String TABLE_ID_FIELD = "TableID";
    public static String TABLE_NUMBER_FIELD = "TableNumber";
    public static String JOINEDSEATSCOUNT_FIELD = "JoinedSeatsCount";
    public static String TOTALSEATSCOUNT_FIELD = "TotalSeatsCount";
    public static String SEARCHDATE_FIELD = "SearchDate";
    public static String REMAININGSEATSCOUNT_FIELD= "RemainingSeatsCount";
    public static String FROMTIME_FIELD = "FromTime";
    public static String TOTIME_FIELD = "ToTime";
    public static String ISOPENTABLE_FIELD = "IsOpenTable";
    public static String ISBOOKEDTABLE_FIELD = "IsBookedTable";
    public static String TABLESTATUSNAME_FIELD = "TableStatusName";
    public static String TABLESTATUSID_FIELD = "TableStatusID";
    public static String BOOKINGID_FIELD  = "BookingID";
    public static String REMAININGDAYS_FIELD = "RemainingDays";
    public static String REMAININGHOURS_FIELD = "RemainingHours";
    public static String REMAININGMINUTES_FIELD = "RemainingMinutes";
    public static String TABLEBOOKINGHOST_USERID_FIELD = "TableBookingHost_UserID";
    public static String TABLEBOOKINGHOST_PROFILEPICTURE_FIELD = "TableBookingHost_ProfilePicture";
    public static String TABLEBOOKINGHOST_TITLE_FIELD = "TableBookingHost_Title";
    public static String TABLEBOOKINGHOST_FIRSTNAME_FIELD = "TableBookingHost_FirstName";
    public static String TABLEBOOKINGHOST_LASTNAME_FIELD = "TableBookingHost_LastName";
    public static String TABLEBOOKINGHOST_AGE_FIELD = "TableBookingHost_Age";
    public static String TABLEBOOKINGHOST_GENDER_FIELD = "TableBookingHost_Gender";
    public static String TABLEBOOKINGHOST_RELATIONSHIPSTATUSNAME = "TableBookingHost_RelationShipStatusName";
    public static String TABLEBOOKINGHOST_LOOKINGFORNAME = "TableBookingHost_LookingForName";
    public static String TABLEBOOKINGHOST_LANGUAGENAME = "TableBookingHost_LanguageName";
    public static String TABLEBOOKINGHOST_ETHNICITYNAME = "TableBookingHost_EthnicityName";
    public static String BOOKING_DATE_FIELD = "BookingDate";
    public static String DATETIMEID_FIELD = "DateTimeID";
    public static String SEATSOPTION_FIELD = "SeatsOption";

    public static String BOOKING_RAW_STRING_FIELD = "BookRowString";

    public static String PENDING_KIND_FIELD = "PendingKind";
    public static String PENDING_POSITION_FIELD = "PendingPosition";

    public static DatabaseHelper __helper = null;
    public static DatabaseHelper getInstance(Context context)
    {
        if( __helper == null )
        {
            __helper = new DatabaseHelper(context);
        }
        return __helper;
    }

    public DatabaseHelper(Context context) {
        super(context, "DineUnite", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try
        {
            db.execSQL(
                    "CREATE TABLE " + SOCIAL_TABLE_NAME + " " +
                            "(Id integer primary key autoincrement, " +
                            RESTAURANT_ID_FIELD  + " integer, " +
                            RESTAURANT_NAME_FIELD  + " text, " +
                            RESTAURANT_IMAGE_FIELD  + " text, " +
                            RESTAURANT_DISTANCE_FIELD  + " double, " +
                            RESTAURANT_POSTCODE_FIELD  + " text, " +
                            RESTAURANT_ADDRESS_FIELD  + " text, " +
                            RESTAURANT_LATITUDE_FIELD  + " double, " +
                            RESTAURANT_LONGITUDE_FIELD  + " double, " +
                            TABLE_ID_FIELD  + " integer, " +
                            TABLE_NUMBER_FIELD  + " integer, " +
                            JOINEDSEATSCOUNT_FIELD  + " integer, " +
                            TOTALSEATSCOUNT_FIELD  + " integer, " +
                            SEARCHDATE_FIELD  + " text, " +
                            REMAININGSEATSCOUNT_FIELD + " integer, " +
                            FROMTIME_FIELD  + " text, " +
                            TOTIME_FIELD  + " text, " +
                            ISOPENTABLE_FIELD  + " integer, " +
                            ISBOOKEDTABLE_FIELD  + " integer, " +
                            TABLESTATUSID_FIELD + " integer, " +
                            TABLESTATUSNAME_FIELD  + " text, " +
                            BOOKINGID_FIELD   + " integer, " +
                            REMAININGDAYS_FIELD  + " integer, " +
                            REMAININGHOURS_FIELD  + " integer, " +
                            REMAININGMINUTES_FIELD  + " integer, " +
                            TABLEBOOKINGHOST_USERID_FIELD  + " integer, " +
                            TABLEBOOKINGHOST_PROFILEPICTURE_FIELD   + " text, " +
                            TABLEBOOKINGHOST_TITLE_FIELD   + " text, " +
                            TABLEBOOKINGHOST_FIRSTNAME_FIELD   + " text, " +
                            TABLEBOOKINGHOST_LASTNAME_FIELD   + " text, " +
                            TABLEBOOKINGHOST_AGE_FIELD   + " integer, " +
                            TABLEBOOKINGHOST_GENDER_FIELD   + " text, " +
                            TABLEBOOKINGHOST_RELATIONSHIPSTATUSNAME   + " text, " +
                            TABLEBOOKINGHOST_LOOKINGFORNAME   + " text, " +
                            TABLEBOOKINGHOST_LANGUAGENAME   + " text, " +
                            TABLEBOOKINGHOST_ETHNICITYNAME   + " text, " +
                            BOOKING_DATE_FIELD + " text, " +
                            DATETIMEID_FIELD + " integer, " +
                            SEATSOPTION_FIELD + " text)"
            );

            db.execSQL(
                    "CREATE TABLE " + RESTAURANT_TABLE_NAME + " " +
                            "(Id integer primary key autoincrement, " +
                            RESTAURANT_ID_FIELD  + " integer, " +
                            RESTAURANT_NAME_FIELD  + " text, " +
                            RESTAURANT_IMAGE_FIELD  + " text, " +
                            RESTAURANT_DISTANCE_FIELD  + " double, " +
                            RESTAURANT_POSTCODE_FIELD  + " text, " +
                            RESTAURANT_ADDRESS_FIELD  + " text, " +
                            RESTAURANT_LATITUDE_FIELD  + " double, " +
                            RESTAURANT_LONGITUDE_FIELD  + " double, " +
                            TABLE_ID_FIELD  + " integer, " +
                            TABLE_NUMBER_FIELD  + " integer, " +
                            JOINEDSEATSCOUNT_FIELD  + " integer, " +
                            TOTALSEATSCOUNT_FIELD  + " integer, " +
                            SEARCHDATE_FIELD  + " text, " +
                            REMAININGSEATSCOUNT_FIELD + " integer, " +
                            FROMTIME_FIELD  + " text, " +
                            TOTIME_FIELD  + " text, " +
                            ISOPENTABLE_FIELD  + " integer, " +
                            ISBOOKEDTABLE_FIELD  + " integer, " +
                            TABLESTATUSID_FIELD + " integer, " +
                            TABLESTATUSNAME_FIELD  + " text, " +
                            BOOKINGID_FIELD   + " integer, " +
                            REMAININGDAYS_FIELD  + " integer, " +
                            REMAININGHOURS_FIELD  + " integer, " +
                            REMAININGMINUTES_FIELD  + " integer, " +
                            TABLEBOOKINGHOST_USERID_FIELD  + " integer, " +
                            TABLEBOOKINGHOST_PROFILEPICTURE_FIELD   + " text, " +
                            TABLEBOOKINGHOST_TITLE_FIELD   + " text, " +
                            TABLEBOOKINGHOST_FIRSTNAME_FIELD   + " text, " +
                            TABLEBOOKINGHOST_LASTNAME_FIELD   + " text, " +
                            TABLEBOOKINGHOST_AGE_FIELD   + " integer, " +
                            TABLEBOOKINGHOST_GENDER_FIELD   + " text, " +
                            TABLEBOOKINGHOST_RELATIONSHIPSTATUSNAME   + " text, " +
                            TABLEBOOKINGHOST_LOOKINGFORNAME   + " text, " +
                            TABLEBOOKINGHOST_LANGUAGENAME   + " text, " +
                            TABLEBOOKINGHOST_ETHNICITYNAME   + " text," +
                            BOOKING_DATE_FIELD + " text, " +
                            DATETIMEID_FIELD + " integer, " +
                            SEATSOPTION_FIELD + " text)"
            );

            db.execSQL("CREATE TABLE " + BOOKING_TABLE_NAME + " " + "(Id integer primary key autoincrement, " +
                    BOOKING_RAW_STRING_FIELD + " text)"
            );

            db.execSQL("CREATE TABLE " + PENDING_TABLE_NAME + " " + "(Id integer primary key autoincrement, " +
                    PENDING_KIND_FIELD + " integer, " + PENDING_POSITION_FIELD + " integer)"
            );

        }catch(Exception e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try
        {
            db.execSQL("DROP TABLE IF EXISTS " + SOCIAL_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RESTAURANT_TABLE_NAME);
            onCreate(db);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public List<FoundTableInfo> getSocialTable()
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + SOCIAL_TABLE_NAME, null);

            ArrayList<FoundTableInfo> items = new ArrayList<>();

            if( cursor != null && cursor.getCount() > 0 )
            {
                cursor.moveToFirst();
                for(int i = 0; i < cursor.getCount(); i++)
                {
                    FoundTableInfo info = new FoundTableInfo();
                    info.RestaurantID = cursor.getInt(cursor.getColumnIndex(RESTAURANT_ID_FIELD));
                    info.RestaurantName = cursor.getString(cursor.getColumnIndex(RESTAURANT_NAME_FIELD ));
                    info.RestaurantImage = cursor.getString(cursor.getColumnIndex(RESTAURANT_IMAGE_FIELD ));
                    info.RestaurantDistance = cursor.getInt(cursor.getColumnIndex(RESTAURANT_DISTANCE_FIELD ));
                    info.PostCode = cursor.getString(cursor.getColumnIndex(RESTAURANT_POSTCODE_FIELD ));
                    info.RestaurantAddress = cursor.getString(cursor.getColumnIndex(RESTAURANT_ADDRESS_FIELD ));
                    info.RestaurantLatitude = cursor.getDouble(cursor.getColumnIndex(RESTAURANT_LATITUDE_FIELD ));
                    info.RestaurantLongitude = cursor.getDouble(cursor.getColumnIndex(RESTAURANT_LONGITUDE_FIELD ));
                    info.TableID = cursor.getInt(cursor.getColumnIndex(TABLE_ID_FIELD ));
                    info.TableNumber = cursor.getInt(cursor.getColumnIndex(TABLE_NUMBER_FIELD ));
                    info.JoinedSeatsCount = cursor.getInt(cursor.getColumnIndex(JOINEDSEATSCOUNT_FIELD ));
                    info.TotalSeatsCount = cursor.getInt(cursor.getColumnIndex(TOTALSEATSCOUNT_FIELD ));
                    info.SearchDate = cursor.getString(cursor.getColumnIndex(SEARCHDATE_FIELD ));
                    info.RemainingSeatsCount = cursor.getInt(cursor.getColumnIndex(REMAININGSEATSCOUNT_FIELD));
                    info.FromTime = cursor.getString(cursor.getColumnIndex(FROMTIME_FIELD ));
                    info.ToTime = cursor.getString(cursor.getColumnIndex(TOTIME_FIELD ));
                    info.IsOpenTable = cursor.getInt(cursor.getColumnIndex(ISOPENTABLE_FIELD)) == 1 ? true : false;
                    info.IsBookedTable = cursor.getInt(cursor.getColumnIndex(ISBOOKEDTABLE_FIELD )) == 1 ? true : false;
                    info.TableStatusID = cursor.getInt(cursor.getColumnIndex(TABLESTATUSID_FIELD ));
                    info.TableStatusName = cursor.getString(cursor.getColumnIndex(TABLESTATUSNAME_FIELD ));
                    info.BookingID = cursor.getInt(cursor.getColumnIndex(BOOKINGID_FIELD));
                    info.RemaininDays = cursor.getInt(cursor.getColumnIndex(REMAININGDAYS_FIELD));
                    info.RemainingHours = cursor.getInt(cursor.getColumnIndex(REMAININGHOURS_FIELD));
                    info.RemainingMinutes = cursor.getInt(cursor.getColumnIndex(REMAININGMINUTES_FIELD));
                    info.Host = new TableBookingHost();
                    info.Host.UserID = cursor.getInt(cursor.getColumnIndex(TABLEBOOKINGHOST_USERID_FIELD));
                    info.Host.ProfilePicture = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_PROFILEPICTURE_FIELD));
                    info.Host.Title = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_TITLE_FIELD));
                    info.Host.FirstName = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_FIRSTNAME_FIELD));
                    info.Host.LastName = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_LASTNAME_FIELD));
                    info.Host.Age = cursor.getInt(cursor.getColumnIndex(TABLEBOOKINGHOST_AGE_FIELD));
                    info.Host.Gender = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_GENDER_FIELD));
                    info.Host.RelationshipStatusName = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_RELATIONSHIPSTATUSNAME));
                    info.Host.LookingForName = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_LOOKINGFORNAME));
                    info.Host.LanguageName = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_LANGUAGENAME));
                    info.Host.EthnicityName = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_ETHNICITYNAME));
                    info.BookingDate = cursor.getString(cursor.getColumnIndex(BOOKING_DATE_FIELD));
                    info.DateTimeID = cursor.getInt(cursor.getColumnIndex(DATETIMEID_FIELD));
                    info.SeatsOption = cursor.getString(cursor.getColumnIndex(SEATSOPTION_FIELD));
                    cursor.moveToNext();
                    items.add(info);
                }
            }
            db.close();
            return items;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }

    public void replaceSocialTable(List<FoundTableInfo> items)
    {
        try
        {
            if(items == null) return;
            SQLiteDatabase db = getWritableDatabase();

            for(FoundTableInfo info : items)
            {
                ContentValues values = new ContentValues();
                values.put(RESTAURANT_ID_FIELD , (int)info.RestaurantID);
                values.put(RESTAURANT_NAME_FIELD , info.RestaurantName);
                values.put(RESTAURANT_IMAGE_FIELD  , info.RestaurantImage);
                values.put(RESTAURANT_DISTANCE_FIELD  , info.RestaurantDistance);
                values.put(RESTAURANT_POSTCODE_FIELD  , info.PostCode);
                values.put(RESTAURANT_ADDRESS_FIELD  , info.RestaurantAddress);
                values.put(RESTAURANT_LATITUDE_FIELD  , info.RestaurantLatitude);
                values.put(RESTAURANT_LONGITUDE_FIELD  , info.RestaurantLongitude);
                values.put(TABLE_ID_FIELD  , info.TableID);
                values.put(TABLE_NUMBER_FIELD  , info.TableNumber);
                values.put(JOINEDSEATSCOUNT_FIELD  , info.JoinedSeatsCount);
                values.put(TOTALSEATSCOUNT_FIELD  , info.TotalSeatsCount);
                values.put(SEARCHDATE_FIELD , info.SearchDate);
                values.put(REMAININGSEATSCOUNT_FIELD , info.RemainingSeatsCount);
                values.put(FROMTIME_FIELD  , info.FromTime);
                values.put(TOTIME_FIELD  , info.ToTime);
                values.put(ISOPENTABLE_FIELD  , info.IsOpenTable ? 1 : 0);
                values.put(ISBOOKEDTABLE_FIELD , info.IsBookedTable ? 1 : 0);
                values.put(TABLESTATUSNAME_FIELD  , info.TableStatusName);
                values.put(TABLESTATUSID_FIELD, info.TableStatusID);
                values.put(BOOKINGID_FIELD  , info.BookingID);
                values.put(REMAININGDAYS_FIELD  , info.RemaininDays);
                values.put(REMAININGHOURS_FIELD  , info.RemainingHours);
                values.put(REMAININGMINUTES_FIELD  , info.RemainingMinutes);
                values.put(TABLEBOOKINGHOST_USERID_FIELD  , info.Host.UserID);
                values.put(TABLEBOOKINGHOST_PROFILEPICTURE_FIELD  , info.Host.ProfilePicture);
                values.put(TABLEBOOKINGHOST_TITLE_FIELD  , info.Host.Title);
                values.put(TABLEBOOKINGHOST_FIRSTNAME_FIELD  , info.Host.FirstName);
                values.put(TABLEBOOKINGHOST_LASTNAME_FIELD  , info.Host.LastName);
                values.put(TABLEBOOKINGHOST_GENDER_FIELD  , info.Host.Gender);
                values.put(TABLEBOOKINGHOST_AGE_FIELD  , info.Host.Age);
                values.put(TABLEBOOKINGHOST_RELATIONSHIPSTATUSNAME  , info.Host.RelationshipStatusName);
                values.put(TABLEBOOKINGHOST_LOOKINGFORNAME  , info.Host.LookingForName);
                values.put(TABLEBOOKINGHOST_LANGUAGENAME  , info.Host.LanguageName);
                values.put(TABLEBOOKINGHOST_ETHNICITYNAME  , info.Host.EthnicityName);
                values.put(BOOKING_DATE_FIELD, info.BookingDate);
                values.put(DATETIMEID_FIELD, info.DateTimeID);
                values.put(SEATSOPTION_FIELD, info.SeatsOption);
                db.insert(SOCIAL_TABLE_NAME, null, values);
            }
            db.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public List<FoundTableInfo> getRestaurantTable()
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + RESTAURANT_TABLE_NAME, null);

            ArrayList<FoundTableInfo> items = new ArrayList<>();

            if( cursor != null && cursor.getCount() > 0 )
            {
                cursor.moveToFirst();
                for(int i = 0; i < cursor.getCount(); i++)
                {
                    FoundTableInfo info = new FoundTableInfo();
                    info.RestaurantID = cursor.getInt(cursor.getColumnIndex(RESTAURANT_ID_FIELD));
                    info.RestaurantName = cursor.getString(cursor.getColumnIndex(RESTAURANT_NAME_FIELD ));
                    info.RestaurantImage = cursor.getString(cursor.getColumnIndex(RESTAURANT_IMAGE_FIELD ));
                    info.RestaurantDistance = cursor.getInt(cursor.getColumnIndex(RESTAURANT_DISTANCE_FIELD ));
                    info.PostCode = cursor.getString(cursor.getColumnIndex(RESTAURANT_POSTCODE_FIELD ));
                    info.RestaurantAddress = cursor.getString(cursor.getColumnIndex(RESTAURANT_ADDRESS_FIELD ));
                    info.RestaurantLatitude = cursor.getDouble(cursor.getColumnIndex(RESTAURANT_LATITUDE_FIELD ));
                    info.RestaurantLongitude = cursor.getDouble(cursor.getColumnIndex(RESTAURANT_LONGITUDE_FIELD ));
                    info.TableID = cursor.getInt(cursor.getColumnIndex(TABLE_ID_FIELD ));
                    info.TableNumber = cursor.getInt(cursor.getColumnIndex(TABLE_NUMBER_FIELD ));
                    info.JoinedSeatsCount = cursor.getInt(cursor.getColumnIndex(JOINEDSEATSCOUNT_FIELD ));
                    info.TotalSeatsCount = cursor.getInt(cursor.getColumnIndex(TOTALSEATSCOUNT_FIELD ));
                    info.SearchDate = cursor.getString(cursor.getColumnIndex(SEARCHDATE_FIELD ));
                    info.RemainingSeatsCount = cursor.getInt(cursor.getColumnIndex(REMAININGSEATSCOUNT_FIELD));
                    info.FromTime = cursor.getString(cursor.getColumnIndex(FROMTIME_FIELD ));
                    info.ToTime = cursor.getString(cursor.getColumnIndex(TOTIME_FIELD ));
                    info.IsOpenTable = cursor.getInt(cursor.getColumnIndex(ISOPENTABLE_FIELD)) == 1 ? true : false;
                    info.IsBookedTable = cursor.getInt(cursor.getColumnIndex(ISBOOKEDTABLE_FIELD )) == 1 ? true : false;
                    info.TableStatusID = cursor.getInt(cursor.getColumnIndex(TABLESTATUSID_FIELD ));
                    info.TableStatusName = cursor.getString(cursor.getColumnIndex(TABLESTATUSNAME_FIELD ));
                    info.BookingID = cursor.getInt(cursor.getColumnIndex(BOOKINGID_FIELD));
                    info.RemaininDays = cursor.getInt(cursor.getColumnIndex(REMAININGDAYS_FIELD));
                    info.RemainingHours = cursor.getInt(cursor.getColumnIndex(REMAININGHOURS_FIELD));
                    info.RemainingMinutes = cursor.getInt(cursor.getColumnIndex(REMAININGMINUTES_FIELD));
                    info.Host = new TableBookingHost();
                    info.Host.UserID = cursor.getInt(cursor.getColumnIndex(TABLEBOOKINGHOST_USERID_FIELD));
                    info.Host.ProfilePicture = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_PROFILEPICTURE_FIELD));
                    info.Host.Title = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_TITLE_FIELD));
                    info.Host.FirstName = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_FIRSTNAME_FIELD));
                    info.Host.LastName = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_LASTNAME_FIELD));
                    info.Host.Age = cursor.getInt(cursor.getColumnIndex(TABLEBOOKINGHOST_AGE_FIELD));
                    info.Host.Gender = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_GENDER_FIELD));
                    info.Host.RelationshipStatusName = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_RELATIONSHIPSTATUSNAME));
                    info.Host.LookingForName = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_LOOKINGFORNAME));
                    info.Host.LanguageName = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_LANGUAGENAME));
                    info.Host.EthnicityName = cursor.getString(cursor.getColumnIndex(TABLEBOOKINGHOST_ETHNICITYNAME));
                    info.BookingDate = cursor.getString(cursor.getColumnIndex(BOOKING_DATE_FIELD));
                    info.DateTimeID = cursor.getInt(cursor.getColumnIndex(DATETIMEID_FIELD));
                    info.SeatsOption = cursor.getString(cursor.getColumnIndex(SEATSOPTION_FIELD));
                    cursor.moveToNext();
                    items.add(info);
                }
            }
            db.close();
            return items;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void replaceRestaurantTable(List<FoundTableInfo> items)
    {
        try
        {
            if(items == null) return;
            SQLiteDatabase db = getWritableDatabase();

            for(FoundTableInfo info : items)
            {
                ContentValues values = new ContentValues();
                values.put(RESTAURANT_ID_FIELD , (int)info.RestaurantID);
                values.put(RESTAURANT_NAME_FIELD , info.RestaurantName);
                values.put(RESTAURANT_IMAGE_FIELD  , info.RestaurantImage);
                values.put(RESTAURANT_DISTANCE_FIELD  , info.RestaurantDistance);
                values.put(RESTAURANT_POSTCODE_FIELD  , info.PostCode);
                values.put(RESTAURANT_ADDRESS_FIELD  , info.RestaurantAddress);
                values.put(RESTAURANT_LATITUDE_FIELD  , info.RestaurantLatitude);
                values.put(RESTAURANT_LONGITUDE_FIELD  , info.RestaurantLongitude);
                values.put(TABLE_ID_FIELD  , info.TableID);
                values.put(TABLE_NUMBER_FIELD  , info.TableNumber);
                values.put(JOINEDSEATSCOUNT_FIELD  , info.JoinedSeatsCount);
                values.put(TOTALSEATSCOUNT_FIELD  , info.TotalSeatsCount);
                values.put(SEARCHDATE_FIELD , info.SearchDate);
                values.put(REMAININGSEATSCOUNT_FIELD , info.RemainingSeatsCount);
                values.put(FROMTIME_FIELD  , info.FromTime);
                values.put(TOTIME_FIELD  , info.ToTime);
                values.put(ISOPENTABLE_FIELD  , info.IsOpenTable ? 1 : 0);
                values.put(ISBOOKEDTABLE_FIELD , info.IsBookedTable ? 1 : 0);
                values.put(TABLESTATUSNAME_FIELD  , info.TableStatusName);
                values.put(TABLESTATUSID_FIELD, info.TableStatusID);
                values.put(BOOKINGID_FIELD  , info.BookingID);
                values.put(REMAININGDAYS_FIELD  , info.RemaininDays);
                values.put(REMAININGHOURS_FIELD  , info.RemainingHours);
                values.put(REMAININGMINUTES_FIELD  , info.RemainingMinutes);
                if(info.Host == null)
                {
                    info.Host = new TableBookingHost();
                }
                values.put(TABLEBOOKINGHOST_USERID_FIELD  , info.Host.UserID);
                values.put(TABLEBOOKINGHOST_PROFILEPICTURE_FIELD  , info.Host.ProfilePicture);
                values.put(TABLEBOOKINGHOST_TITLE_FIELD  , info.Host.Title);
                values.put(TABLEBOOKINGHOST_FIRSTNAME_FIELD  , info.Host.FirstName);
                values.put(TABLEBOOKINGHOST_LASTNAME_FIELD  , info.Host.LastName);
                values.put(TABLEBOOKINGHOST_GENDER_FIELD  , info.Host.Gender);
                values.put(TABLEBOOKINGHOST_AGE_FIELD  , info.Host.Age);
                values.put(TABLEBOOKINGHOST_RELATIONSHIPSTATUSNAME  , info.Host.RelationshipStatusName);
                values.put(TABLEBOOKINGHOST_LOOKINGFORNAME  , info.Host.LookingForName);
                values.put(TABLEBOOKINGHOST_LANGUAGENAME  , info.Host.LanguageName);
                values.put(TABLEBOOKINGHOST_ETHNICITYNAME  , info.Host.EthnicityName);
                values.put(BOOKING_DATE_FIELD, info.BookingDate);
                values.put(DATETIMEID_FIELD, info.DateTimeID);
                values.put(SEATSOPTION_FIELD, info.SeatsOption);
                db.insert(RESTAURANT_TABLE_NAME, null, values);
            }
            db.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public void deleteAllTables() {
        try
        {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(SOCIAL_TABLE_NAME, null, null);
            db.delete(RESTAURANT_TABLE_NAME, null, null);
            db.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void registerBookingList(String bookings) {
        try
        {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(BOOKING_TABLE_NAME, null, null);

            ContentValues values = new ContentValues();
            values.put(BOOKING_RAW_STRING_FIELD, bookings);
            db.insert(BOOKING_TABLE_NAME, null, values);
            db.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public String getBookingList() {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + BOOKING_TABLE_NAME, null);
            if(cursor == null || cursor.getCount() == 0 ) return null;
            cursor.moveToFirst();
            String strValue = cursor.getString(cursor.getColumnIndex(BOOKING_RAW_STRING_FIELD ));
            db.close();
            return strValue;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public void addPendingTask(int mType, int nTmpPosition) {
        try
        {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PENDING_KIND_FIELD , mType);
            values.put(PENDING_POSITION_FIELD, nTmpPosition);
            db.insert(PENDING_TABLE_NAME, null, values);
            db.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public int getPendingCount() {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + PENDING_TABLE_NAME, null);
            if(cursor == null || cursor.getCount() == 0 ) return 0;
            int nCount = cursor.getCount();
            db.close();
            return nCount;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Integer> getPendingPosition(int nType) {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + PENDING_POSITION_FIELD + " FROM " + PENDING_TABLE_NAME + " WHERE "
                    + PENDING_KIND_FIELD + "=" + nType, null);
            List<Integer> values = new ArrayList<>();
            if( cursor != null && cursor.getCount() > 0 ) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    int nPosition = cursor.getInt(cursor.getColumnIndex(PENDING_POSITION_FIELD));
                    Integer value = new Integer(nPosition);
                    values.add(value);
                }
            }
            return values;
        }catch(Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void removePendingList()
    {
        try
        {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(PENDING_TABLE_NAME, null, null);
            db.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}
