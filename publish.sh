#
# commands needed to publish to app store
# expects unencrypted apk in current dir
#
# NOTE: paths probably need to be checked/changed depending on local installation!!!!

jarsigner -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore digitalica.keystore SkydiveKompasroos.apk digitalica

mv SkydiveKompasroos.apk SkydiveKompasroosUnaligned.apk

./Downloads/android-sdk-linux/tools/zipalign -f -v 4 SkydiveKompasroosUnaligned.apk SkydiveKompasroos.apk 

