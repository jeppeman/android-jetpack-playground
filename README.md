
[![CircleCI](https://circleci.com/gh/jeppeman/android-jetpack-playground/tree/master.svg?style=svg)](https://circleci.com/gh/jeppeman/android-jetpack-playground/tree/master)

<a href='https://play.google.com/store/apps/details?id=com.jeppeman.jetpackplayground'><img height="50" width="50" alt='Get it on Google Play' src='assets/google-play-store.png'/></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a href='http://apps.samsung.com/appquery/appDetail.as?appId=com.jeppeman.jetpackplayground'><img height="50" width="50" alt='Get it on Huawei App Gallery' src='assets/huawei-app-gallery.png'/></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a href='https://play.google.com/store/apps/details?id=com.jeppeman.jetpackplayground&gl=SE&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img height="50" width="50" alt='Get it on Amazon App Store' src='assets/amazon-app-store.png'/></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a href='https://play.google.com/store/apps/details?id=com.jeppeman.jetpackplayground&gl=SE&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img height="50" width="50" alt='Get it on Samsung Galaxy Store' src='assets/samsung-galaxy-store.png'/></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

# android-jetpack-playground

A small video player pet project with the purpose of fiddling around new Android development features.
Some areas of exploration:
* Dynamic Feature Modules and navigation patterns with them
* MotionLayout
* Jetpack testing, mainly isolated fragment unit tests that run both on device and the JVM with the same source code

App Stores
---
The app is published on the following platforms:
* [Google Play Store](https://play.google.com/store/apps/details?id=com.jeppeman.jetpackplayground)
* [Huawei App Gallery](http://apps.samsung.com/appquery/appDetail.as?appId=com.jeppeman.jetpackplayground)
* [Amazon App Store](http://apps.samsung.com/appquery/appDetail.as?appId=com.jeppeman.jetpackplayground)
* [Samsung Galaxy Store](http://apps.samsung.com/appquery/appDetail.as?appId=com.jeppeman.jetpackplayground)

It leverages dynamic feature modules on all of them, [GloballyDynamic](https://github.com/jeppeman/GloballyDynamic) is what enables this. 

Articles
---
* <a href="https://proandroiddev.com/isolated-fragments-unit-tests-that-run-both-instrumented-and-on-the-jvm-with-the-same-source-code-283db2e9be5d">Runtime agnostic and isolated fragment unit tests</a>
* <a href="https://medium.com/@jesperaamann/navigation-with-dynamic-feature-modules-48ee7645488">Navigation with Dynamic Feature Modules</a>
