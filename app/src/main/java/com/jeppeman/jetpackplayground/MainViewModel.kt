package com.jeppeman.jetpackplayground

import com.jeppeman.jetpackplayground.base.BaseFeatureNavigatorViewModel
import javax.inject.Inject

abstract class MainViewModel : BaseFeatureNavigatorViewModel()

class MainViewModelImpl @Inject constructor() : MainViewModel()