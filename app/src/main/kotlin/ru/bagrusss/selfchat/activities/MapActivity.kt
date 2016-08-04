package ru.bagrusss.selfchat.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.github.clans.fab.FloatingActionButton
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import org.jetbrains.anko.find
import ru.bagrusss.selfchat.R
import ru.bagrusss.selfchat.fragments.FragmentSplash
import ru.bagrusss.selfchat.util.FileStorage

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    var mMap: GoogleMap? = null
    var mFab: FloatingActionButton? = null
    var mMapFragment: MapFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mFab = find(R.id.fab_ok)
        mFab?.setOnClickListener {
            mMap?.snapshot({
                saveMapBmp(it)
            })
        }
        fragmentManager.beginTransaction().replace(R.id.content, FragmentSplash()).commit()
        mFab?.hide(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Handler().postDelayed({
            mMapFragment = MapFragment.newInstance()
            fragmentManager.beginTransaction().replace(R.id.content, mMapFragment).commit()
            mMapFragment!!.getMapAsync(this)
        }, 1000)
    }

    override fun onMapReady(map: GoogleMap) {
        mFab?.show(false)
        map.uiSettings.isZoomControlsEnabled = true
        map.isMyLocationEnabled = true
        map.isBuildingsEnabled = true
        map.setPadding(0, 0, 0, resources.getDimensionPixelSize(R.dimen.zoom_up))
        mMap = map

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveMapBmp(b: Bitmap) {
        Thread({
            val file = FileStorage.saveBMPtoStorage(b)
            runOnUiThread {
                val intent = Intent()
                intent.data = Uri.parse(file)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }).start()
    }

}
