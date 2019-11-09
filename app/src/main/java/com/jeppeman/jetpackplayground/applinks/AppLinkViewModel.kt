package com.jeppeman.jetpackplayground.applinks

import com.jeppeman.jetpackplayground.base.BaseFeatureNavigatorViewModel
import javax.inject.Inject

abstract class AppLinkViewModel : BaseFeatureNavigatorViewModel()

class AppLinkViewModelImpl @Inject constructor() : AppLinkViewModel()