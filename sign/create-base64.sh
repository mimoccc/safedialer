#!/bin/sh
signing_name="mjdev"
openssl base64 < "$signing_name.cert" | tr -d '\n' > "$signing_name.jks.base64.txt"
# define github secrets
# SIGNING_KEY_BASE64, KEYSTORE_PASSWORD, KEY_PASSWORD, KEY_ALIAS
# steps:
#  - uses: r0adkll/sign-android-release@v1
#    name: Sign app APK
#    # ID used to access action output
#    id: sign_app
#    with:
#      releaseDirectory: app/build/outputs/apk/release
#      signingKeyBase64: ${{ secrets.SIGNING_KEY }}
#      alias: ${{ secrets.ALIAS }}
#      keyStorePassword: ${{ secrets.KEY_STORE_PASSWORD }}
#      keyPassword: ${{ secrets.KEY_PASSWORD }}
#    env:
#      # override default build-tools version (33.0.0) -- optional
#      # BUILD_TOOLS_VERSION: "34.0.0"
#
#  # Example use of `signedReleaseFile` output -- not needed
#  - uses: actions/upload-artifact@v2
#    with:
#      name: Signed app bundle
#      path: ${{steps.sign_app.outputs.signedReleaseFile}}
