// The Hindi storytellers' voice model (Kokoro v1.0, about 90 MB). It is an
// on-demand Play asset pack: only families who pick a Hindi storyteller download it.
plugins {
    alias(libs.plugins.android.asset.pack)
}

assetPack {
    packName.set("hindivoice")
    dynamicDelivery {
        deliveryType.set("on-demand")
    }
}
