package pl.dybuk.posttest.ui


import androidx.fragment.app.FragmentActivity
import pl.dybuk.posttest.CoreApplication



fun FragmentActivity.dagger() = (this.application as CoreApplication).appComponent