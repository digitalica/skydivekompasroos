#
# commands needed to publish to app store
# expects unencrypted apk in current dir
#
# NOTE: paths probably need to be checked/changed depending on local installation!!!!


# To export an unsigned APK from Eclipse, right-click the project in the Package Explorer
# and select Android Tools > Export Unsigned Application Package. Then specify the file
# location for the unsigned APK. (Alternatively, open your AndroidManifest.xml file in
# Eclipse, select the Manifest tab, and click Export an unsigned APK.)

cd /home/robbert

jarsigner -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore digitalica.keystore SkydiveKompasroos.apk digitalica

mv SkydiveKompasroos.apk SkydiveKompasroosUnaligned.apk

./Downloads/android-sdk-linux/tools/zipalign -f -v 4 SkydiveKompasroosUnaligned.apk SkydiveKompasroos.apk 

