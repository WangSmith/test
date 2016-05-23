package com.paulavasile.dineunite.Global;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.paulavasile.dineunite.data.FoundTableInfo;
import com.paulavasile.dineunite.task.AnyTask;
import com.paulavasile.dineunite.task.AnyTaskCallback;
import com.paulavasile.dineunite.utils.Utility;

import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by silve on 5/22/2016.
 */
public class BackgroundProcessor extends Thread{

    private Context mContext;
    public List<AnyTask> refreshTaskList = new ArrayList<>();
    public List<AnyTask> performTaskList = new ArrayList<>();
    CountDownLatch latch = new CountDownLatch(1);

    public static BackgroundProcessor __processor = null;

    public static BackgroundProcessor getBackgroundProcessor(Context context)
    {
        if( __processor == null )__processor = new BackgroundProcessor(context);
        return __processor;
    }

    public static void DestroybackgroundProcessor()
    {
        if( __processor != null )
            __processor.destroyThread();
        __processor = null;
    }

    public int status = 0;
    public BackgroundProcessor(Context context)
    {
        mContext = context;
        settings = mContext.getSharedPreferences("puntatrackingrefs", 0);
        LoadSocialAndRestaurantTable();
    }

    Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg)
        {
            LoadSocialAndRestaurantTable();
        }
    };
    protected SharedPreferences settings = null;
    private void LoadSocialAndRestaurantTable() {
        long loading_time = settings.getLong("loadingTime", 0);
        Calendar c = Calendar.getInstance();
        long distance = 30 * 60 * 1000;
        if( loading_time ==  0 || System.currentTimeMillis() - loading_time > distance)
        {
            DatabaseHelper.getInstance(mContext).deleteAllTables();
            List<AnyTask> tasks = new ArrayList<>();
            for(int i = 0; i < 30; i++)
            {
                {
                    String strSep = "";
                    strSep = Utility.GET_SOCIAL_TABLES;

                    SoapObject request = new SoapObject(Utility.NAMESPACE,
                            strSep);
                    String soapAction = Utility.NAMESPACE + strSep;
                    // --------------------------------------------
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String strDateParam = df.format(c.getTime());
                    request.addProperty("Date", strDateParam);

                    AnyTask getSocialTask = new AnyTask(mContext, soapAction, request,
                            new AnyTaskCallback() {

                                @Override
                                public void onResult(boolean success, Object result) {
                                    if (success) {
                                        SoapSerializationEnvelope envelope = (SoapSerializationEnvelope) result;

                                        SoapPrimitive response;
                                        try {
                                            response = (SoapPrimitive) envelope
                                                    .getResponse();
                                            String responseString = response.toString();
                                            JSONObject respObj = new JSONObject(
                                                    responseString);
                                            if (respObj.has("AllTables")) {
                                                String tableResultString = respObj
                                                        .getString("AllTables");
                                                ArrayList<FoundTableInfo> tableResultList = FoundTableInfo
                                                        .parseListFromString(tableResultString);

                                                if (tableResultList != null
                                                        && tableResultList.size() > 0) {
                                                    DatabaseHelper.getInstance(mContext).replaceSocialTable(tableResultList);
                                                }
                                            } else {
                                            }
                                        } catch (SoapFault e) {
                                        } catch (Exception e) {
                                        }
                                    } else {

                                    }

                                }
                            }, false);
                    tasks.add(getSocialTask);
                }

                {
                    String strSep = "";
                    strSep = Utility.GET_RESTAURANT_TABLES;

                    SoapObject request = new SoapObject(Utility.NAMESPACE,
                            strSep);
                    String soapAction = Utility.NAMESPACE + strSep;
                    // --------------------------------------------
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String strDateParam = df.format(c.getTime());
                    request.addProperty("Date", strDateParam);

                    AnyTask getRestaurantTask = new AnyTask(mContext, soapAction, request,
                            new AnyTaskCallback() {

                                @Override
                                public void onResult(boolean success, Object result) {
                                    if (success) {
                                        SoapSerializationEnvelope envelope = (SoapSerializationEnvelope) result;

                                        SoapPrimitive response;
                                        try {
                                            response = (SoapPrimitive) envelope
                                                    .getResponse();
                                            String responseString = response.toString();
                                            JSONObject respObj = new JSONObject(
                                                    responseString);
                                            if (respObj.has("AllTables")) {
                                                String tableResultString = respObj
                                                        .getString("AllTables");
                                                ArrayList<FoundTableInfo> tableResultList = FoundTableInfo
                                                        .parseListFromString(tableResultString);

                                                if (tableResultList != null
                                                        && tableResultList.size() > 0) {
                                                    DatabaseHelper.getInstance(mContext).replaceRestaurantTable(tableResultList);
                                                }
                                            } else {
                                            }
                                        } catch (SoapFault e) {
                                        } catch (Exception e) {
                                        }
                                    } else {

                                    }
                                }
                            }, false);
                    tasks.add(getRestaurantTask);
                }

                c.add(Calendar.DATE, 1);
            }
            setBackgroundTask(tasks);
            settings.edit().putLong("loadingTime", System.currentTimeMillis()).commit();
        }
        mHandler.sendEmptyMessageDelayed(0, 60 * 1 * 1000);
    }

    public void destroyThread()
    {
        if(latch != null)
        {
            latch.countDown();
        }
        status = 1;
    }

    @Override
    public void run()
    {
        while( true )
        {
            while( performTaskList.size() > 0 )
            {
                AnyTask task = performTaskList.get(0);
                ProcessAsynTask(task);
                performTaskList.remove(task);
            }
            removePending();

            if( refreshTaskList.size() > 0 )
            {
                AnyTask task = refreshTaskList.get(0);
                ProcessAsynTask(task);
                refreshTaskList.remove(task);
            }
            else
            {
                latch = new CountDownLatch(1);
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if( status == 1 && refreshTaskList.size() == 0 && performTaskList.size() == 0 )
                return;
        }
    }

    public void setBackgroundTask(List<AnyTask> refreshTaskList)
    {
        if( this.refreshTaskList.size() > 0 ) return;
        for(AnyTask task : refreshTaskList)
        {
            this.refreshTaskList.add(task);
        }
        if( latch.getCount() > 0 )
            latch.countDown();
    }

    public void setPerformTask(AnyTask task)
    {
        performTaskList.add(task);
        if( latch.getCount() > 0)
            latch.countDown();
    }

    public void ProcessAsynTask(AnyTask task)
    {
        while( !GlobalHelper.isConnectingToInternet(mContext) )
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        task.setProcessFlag(true);
        task.execute();
        CountDownLatch latch = task.getProcessFlag();
        if( latch != null)
        {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        task.setProcessFlag(false);
    }

    public boolean isRemainingTask() {
        if( refreshTaskList.size() > 0 )
            return true;
        else
            return false;
    }

    public void addPendingTask(int mType, int nTmpPosition) {
        DatabaseHelper.getInstance(mContext).addPendingTask(mType, nTmpPosition);
    }

    public int getPendingTaskListCount()
    {
        return DatabaseHelper.getInstance(mContext).getPendingCount();
    }

    public List<Integer> getPendingPosition(int nType)
    {
        return DatabaseHelper.getInstance(mContext).getPendingPosition(nType);
    }

    public void removePending()
    {
        if(getPendingTaskListCount() > 0 )
        {
            DatabaseHelper.getInstance(mContext).removePendingList();
            try {
                if( GlobalHelper.getMainActivity() != null )
                {
                    GlobalHelper.getMainActivity().refreshFragment();
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }

        }
    }
}
