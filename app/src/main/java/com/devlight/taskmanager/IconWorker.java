package com.devlight.taskmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.devlight.task.Task;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

//class for save load, unload icons
public class IconWorker {

    BitmapFactory.Options options = new BitmapFactory.Options();

    ArrayList<String> mPathList = new ArrayList<String>();
    ArrayList<Long> mIDList = new ArrayList<Long>();
    ArrayList<Bitmap> mBitmapList = new ArrayList<Bitmap>();


    boolean isLoading = true;


    public IconWorker(TaskRecycleAdapter mAdapter, RealmResults<Task> realmResults)
    {
        for (int i=0;i<realmResults.size();i++)
        {
            mPathList.add( realmResults.get(i).getAvatarPath() );
            mIDList.add( realmResults.get(i).getID() );
            mBitmapList.add(null);
        }

        initialInit(mAdapter);
    }


    public void free()
    {
        for (int i=0;i<mBitmapList.size();i++)
            if (mBitmapList.get(i)!=null)
                if (!mBitmapList.get(i).isRecycled())
                {
                    mBitmapList.get(i).recycle();
                    mBitmapList.set(i,null);
                }

        mBitmapList.clear();
        mIDList.clear();
        mPathList.clear();
    }


    public void deleteBitmap(long mID)
    {
        for (int i=0;i<mIDList.size();i++)
            if (mIDList.get(i) == mID)
            {
                if (mBitmapList.get(i)!=null)
                    if (!mBitmapList.get(i).isRecycled())
                    {
                        mBitmapList.get(i).recycle();
                        mBitmapList.set(i,null);
                    }

                mIDList.remove(i);
                mBitmapList.remove(i);
                mPathList.remove(i);
                break;
            }
    }


    public Bitmap getBitmap(long mID,final String curPath)
    {
        for (int i=0;i<mIDList.size();i++)
            if (mIDList.get(i) == mID)
            {

                if (curPath == null ) return null;
                else
                {
                    if (curPath.equals(mPathList.get(i))) return mBitmapList.get(i); //found actual bitmap and return

                    //wee need load new bitmap

                    //free old bitmap
                    if (mBitmapList.get(i)!=null)
                        if (!mBitmapList.get(i).isRecycled())
                        {
                            mBitmapList.get(i).recycle();
                            mBitmapList.set(i,null);
                        }


                    //update path
                    mPathList.set(i,curPath);


                    //load new
                    try {

                        Bitmap mBmp = BitmapFactory.decodeFile(curPath, options);
                        mBitmapList.set(i,mBmp);
                        //if (mBmp!=null) Log.e("xxxxxxxxxxxxx","loaded icon for id="+Long.toString(mID));
                    }catch (Exception e)
                    {
                        mBitmapList.set(i,null);
                        continue;
                    }

                    return mBitmapList.get(i);
                }

            } //if (mIDList.get(i) == mID)


        //id not found
        Bitmap mBmp =  null;
        try {
            mBmp = BitmapFactory.decodeFile(curPath, options);
         //   if (mBmp!=null) Log.e("xxxxxxxxxxxxx","loaded icon for id="+Long.toString(mID));
        }catch (Exception e)
        {
            mBmp =  null;
        }

        mIDList.add(mID);
        mPathList.add(curPath);
        mBitmapList.add(mBmp);


        return mBmp;
    }


    void initialInit(final TaskRecycleAdapter mAdapter)
    {
        new Thread(new Runnable() {
            public void run() {
                ///////////////////////////////////////////////////

                isLoading = true;

                for (int i=0;i<mIDList.size();i++)
                {
                    if (mPathList.get(i)==null) continue;


                    try {

                        Bitmap mBmp = BitmapFactory.decodeFile(mPathList.get(i), options);
                        mBitmapList.set(i,mBmp);
                        // Log.e("xxxxxxxxxxxxx","loaded icon for id="+Long.toString(mIDList.get(i)));
                    }catch (Exception e)
                    {
                        mBitmapList.set(i,null);
                        continue;
                    }

                }//for

                isLoading = false;


                mAdapter.mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });

                ///////////////////////////////////////////////////
            }
        }).start();
    }

    public boolean isLoading() {
        return isLoading;
    }

/*

    public void loadIcons()
    {

        final long idList[] = new long[realmResults.size()];
        final String pathList[] = new String[realmResults.size()];
        final Bitmap bmpList[] = new Bitmap[realmResults.size()];

        realmResults.load();

        Realm instance =((MApp)mContext.getApplicationContext()).realm;
        instance.beginTransaction();
        for (int i=0;i<realmResults.size();i++)
        {
            idList[i] = realmResults.get(i).getID();
            pathList[i] = realmResults.get(i).getAvatarPath();


            //	Log.e("realmResults",Integer.toString(realmResults.get(i).test));
        }
        instance.commitTransaction();




    }
*/






}
