@file:OptIn(ExperimentalForeignApi::class)

package com.cleveradssolutions.plugin.kmp.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.CleverAdsSolutions_Base.CASChoicesView
import cocoapods.CleverAdsSolutions_Base.CASMediaView
import cocoapods.CleverAdsSolutions_Base.CASNativeView
import cocoapods.CleverAdsSolutions_Base.CASStarRatingView
import com.cleveradssolutions.plugin.kmp.CASBannerSize
import com.cleveradssolutions.plugin.kmp.CASNativeAdContent
import com.cleveradssolutions.plugin.kmp.CASNativeScaleType
import com.cleveradssolutions.plugin.kmp.CASNativeTemplateFontStyle
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.UIKit.NSLineBreakByClipping
import platform.UIKit.NSLineBreakByTruncatingTail
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.NSTextAlignmentJustified
import platform.UIKit.NSTextAlignmentLeft
import platform.UIKit.NSTextAlignmentNatural
import platform.UIKit.NSTextAlignmentRight
import platform.UIKit.UIButton
import platform.UIKit.UIButtonTypeSystem
import platform.UIKit.UIColor
import platform.UIKit.UIControlStateNormal
import platform.UIKit.UIFont
import platform.UIKit.UIImageView
import platform.UIKit.UILabel
import platform.UIKit.UIViewContentMode
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
public actual fun CASNativeAdTemplate(
    adContent: CASNativeAdContent,
    maxWidth: Dp,
    maxHeight: Dp,
    modifier: Modifier,
    backgroundColor: Color?,
    primaryColor: Color?,
    primaryTextColor: Color?,
    headlineTextColor: Color?,
    headlineFontStyle: CASNativeTemplateFontStyle?,
    secondaryTextColor: Color?,
    secondaryFontStyle: CASNativeTemplateFontStyle?,
) {
    key(adContent, maxWidth, maxHeight) {
        UIKitView(
            modifier = modifier,
            factory = {
                CASNativeView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)).apply {
                    setAdTemplateSize(
                        CASBannerSize.getInlineBanner(maxWidth.value.toInt(), maxHeight.value.toInt()).ios,
                    )
                    bindAdContent(adContent.ios, autoPopulateText = true)
                }
            },
            update = { view ->
                view.applyKmpTemplateStyle(
                    backgroundColor = backgroundColor,
                    primaryColor = primaryColor,
                    primaryTextColor = primaryTextColor,
                    headlineTextColor = headlineTextColor,
                    headlineFontStyle = headlineFontStyle,
                    secondaryTextColor = secondaryTextColor,
                    secondaryFontStyle = secondaryFontStyle,
                )
            },
        )
    }
}

@Composable
public actual fun CASNativeAd(
    adContent: CASNativeAdContent,
    modifier: Modifier,
    content: @Composable NativeAdScope.() -> Unit,
) {
    val bindingScope = remember(adContent.ios) {
        com.cleveradssolutions.plugin.kmp.composable.NativeAdScope(
            adView = CASNativeView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)).apply {
                clipsToBounds = false
            },
            adContent = adContent
        )
    }

    UIKitView(
        modifier = modifier,
        factory = {
            bindingScope.adView
        },
        update = { view ->
            bindingScope.scheduleBind()
        },
    )
}

public actual class NativeAdScope internal constructor(
    internal val adView: CASNativeView,
    public actual val adContent: CASNativeAdContent
) {
    private var scheduled: Boolean = false
    private var bound: Boolean = false

    internal var headlineView: UILabel? = null
    internal var advertiserView: UILabel? = null
    internal var bodyView: UILabel? = null
    internal var priceView: UILabel? = null
    internal var reviewCountView: UILabel? = null
    internal var starRatingView: CASStarRatingView? = null
    internal var storeView: UILabel? = null
    internal var adLabelView: UILabel? = null
    internal var iconView: UIImageView? = null
    internal var mediaView: CASMediaView? = null
    internal var adChoicesView: CASChoicesView? = null
    internal var callToActionView: UIButton? = null

    internal fun scheduleBind() {
        if (bound || scheduled) return

        scheduled = true

        dispatch_async(dispatch_get_main_queue()) {
            dispatch_async(dispatch_get_main_queue()) {
                scheduled = false

                if (bound) return@dispatch_async

                bound = true
                adView.bindAdContent(adContent.ios)
            }
        }
    }
}

@Composable
public actual fun NativeAdScope.AdChoices(
    modifier: Modifier,
) {
    UIKitView(
        modifier = modifier,
        factory = {
            adChoicesView ?: CASChoicesView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)).also { view ->
                adChoicesView = view
                adView.setAdChoicesView(view)
                scheduleBind()
            }
        },
        update = { view ->
            adChoicesView = view
            adView.setAdChoicesView(view)
            scheduleBind()
        },
    )
}

@Composable
public actual fun NativeAdScope.AdHeadlineText(
    modifier: Modifier,
    color: Color,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
    softWrap: Boolean,
    maxLines: Int,
    minLines: Int,
) {
    UIKitView(
        modifier = modifier,
        factory = {
            headlineView ?: UILabel(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)).also { view ->
                headlineView = view
                adView.setHeadlineView(view)
                scheduleBind()
            }
        },
        update = { view ->
            view.text = adContent.headline.orEmpty()
            applyComposeTextStyle(
                view = view,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                textAlign = textAlign,
                lineHeight = lineHeight,
                softWrap = softWrap,
                maxLines = maxLines,
                minLines = minLines,
                defaultColor = defaultHeadlineColor(),
                defaultTextSize = 18.0,
                defaultBold = true,
            )
            headlineView = view
            adView.setHeadlineView(view)
            scheduleBind()
        },
    )
}

@Composable
public actual fun NativeAdScope.AdAdvertiserText(
    modifier: Modifier,
    color: Color,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
    softWrap: Boolean,
    maxLines: Int,
    minLines: Int,
) {
    TextAsset(
        modifier = modifier,
        text = adContent.advertiser,
        currentView = advertiserView,
        defaultColor = defaultPrimaryColor(),
        defaultTextSize = 12.0,
        defaultBold = true,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        textAlign = textAlign,
        lineHeight = lineHeight,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        assign = { view ->
            advertiserView = view
            adView.setAdvertiserView(view)
        },
    )
}

@Composable
public actual fun NativeAdScope.AdStoreText(
    modifier: Modifier,
    color: Color,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
    softWrap: Boolean,
    maxLines: Int,
    minLines: Int,
) {
    TextAsset(
        modifier = modifier,
        text = adContent.store,
        currentView = storeView,
        defaultColor = defaultHeadlineColor(),
        defaultTextSize = 14.0,
        defaultBold = false,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        textAlign = textAlign,
        lineHeight = lineHeight,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        assign = { view ->
            storeView = view
            adView.setStoreView(view)
        },
    )
}

@Composable
public actual fun NativeAdScope.AdLabelText(
    modifier: Modifier,
    color: Color,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
    softWrap: Boolean,
    maxLines: Int,
    minLines: Int,
) {
    val text = adContent.adLabel ?: return

    UIKitView(
        modifier = modifier,
        factory = {
            adLabelView ?: UILabel(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)).also { view ->
                adLabelView = view
                adView.setAdLabelView(view)
                scheduleBind()
            }
        },
        update = { view ->
            view.text = text
            applyComposeTextStyle(
                view = view,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                textAlign = textAlign,
                lineHeight = lineHeight,
                softWrap = softWrap,
                maxLines = maxLines,
                minLines = minLines,
                defaultColor = defaultPrimaryColor(),
                defaultTextSize = 12.0,
                defaultBold = false,
            )
            view.applyAdLabelBackground(color)
            adLabelView = view
            adView.setAdLabelView(view)
            scheduleBind()
        },
    )
}

@Composable
public actual fun NativeAdScope.AdBodyText(
    modifier: Modifier,
    color: Color,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
    softWrap: Boolean,
    maxLines: Int,
    minLines: Int,
) {
    TextAsset(
        modifier = modifier,
        text = adContent.body,
        currentView = bodyView,
        defaultColor = UIColor.colorWithRed(
            red = 51.0 / 255.0,
            green = 65.0 / 255.0,
            blue = 85.0 / 255.0,
            alpha = 1.0,
        ),
        defaultTextSize = 13.0,
        defaultBold = false,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        textAlign = textAlign,
        lineHeight = lineHeight,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        assign = { view ->
            bodyView = view
            adView.setBodyView(view)
        },
    )
}

@Composable
public actual fun NativeAdScope.AdCallToAction(
    modifier: Modifier,
    textColor: Color,
    backgroundColor: Color,
    shape: Shape,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
    softWrap: Boolean,
    maxLines: Int,
    minLines: Int,
) {
    UIKitView(
        modifier = modifier,
        factory = {
            callToActionView ?: UIButton.buttonWithType(UIButtonTypeSystem).also { view ->
                callToActionView = view
                adView.setCallToActionView(view)
                scheduleBind()
            }
        },
        update = { view ->
            view.setTitle(adContent.callToAction ?: "Details", forState = UIControlStateNormal)
            view.setTitleColor(
                textColor.toUIColor(UIColor.whiteColor),
                forState = UIControlStateNormal
            )
            view.backgroundColor = backgroundColor.toUIColor(UIColor.blackColor)
            view.layer.cornerRadius = 12.0
            view.layer.masksToBounds = true

            view.titleLabel?.let { label ->
                applyComposeTextStyle(
                    view = label,
                    color = textColor,
                    fontSize = fontSize,
                    fontStyle = fontStyle,
                    fontWeight = fontWeight,
                    fontFamily = fontFamily,
                    textAlign = textAlign,
                    lineHeight = lineHeight,
                    softWrap = softWrap,
                    maxLines = maxLines,
                    minLines = minLines,
                    defaultColor = UIColor.whiteColor,
                    defaultTextSize = 14.0,
                    defaultBold = true,
                )
            }

            callToActionView = view
            adView.setCallToActionView(view)
            scheduleBind()
        },
    )
}

@Composable
public actual fun NativeAdScope.AdIconImage(
    modifier: Modifier,
) {
    UIKitView(
        modifier = modifier,
        factory = {
            iconView ?: UIImageView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)).also { view ->
                view.contentMode = UIViewContentMode.UIViewContentModeScaleAspectFit
                view.clipsToBounds = true
                iconView = view
                adView.setIconView(view)
                scheduleBind()
            }
        },
        update = { view ->
            view.contentMode = UIViewContentMode.UIViewContentModeScaleAspectFit
            view.clipsToBounds = true
            iconView = view
            adView.setIconView(view)
            scheduleBind()
        },
    )
}

@Composable
public actual fun NativeAdScope.AdMedia(
    modifier: Modifier,
    scaleType: CASNativeScaleType?,
) {
    UIKitView(
        modifier = modifier,
        factory = {
            mediaView ?: CASMediaView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)).also { view ->
                view.backgroundColor = UIColor.colorWithRed(
                    red = 241.0 / 255.0,
                    green = 245.0 / 255.0,
                    blue = 249.0 / 255.0,
                    alpha = 1.0,
                )
                view.contentMode = scaleType.toUIViewContentMode()
                mediaView = view
                adView.setMediaView(view)
                scheduleBind()
            }
        },
        update = { view ->
            view.contentMode = scaleType.toUIViewContentMode()
            mediaView = view
            adView.setMediaView(view)
            scheduleBind()
        },
    )
}

@Composable
public actual fun NativeAdScope.AdPriceText(
    modifier: Modifier,
    color: Color,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
    softWrap: Boolean,
    maxLines: Int,
    minLines: Int,
) {
    TextAsset(
        modifier = modifier,
        text = adContent.price,
        currentView = priceView,
        defaultColor = defaultHeadlineColor(),
        defaultTextSize = 14.0,
        defaultBold = true,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        textAlign = textAlign,
        lineHeight = lineHeight,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        assign = { view ->
            priceView = view
            adView.setPriceView(view)
        },
    )
}

@Composable
public actual fun NativeAdScope.AdStarRating(
    modifier: Modifier,
) {
    UIKitView(
        modifier = modifier,
        factory = {
            starRatingView ?: CASStarRatingView(
                frame = CGRectMake(
                    0.0,
                    0.0,
                    0.0,
                    0.0
                )
            ).also { view ->
                starRatingView = view
                adView.setStarRatingView(view)
                scheduleBind()
            }
        },
        update = { view ->
            starRatingView = view
            adView.setStarRatingView(view)
            scheduleBind()
        },
    )
}

@Composable
public actual fun NativeAdScope.AdReviewCountText(
    modifier: Modifier,
    color: Color,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
    softWrap: Boolean,
    maxLines: Int,
    minLines: Int,
) {
    TextAsset(
        modifier = modifier,
        text = adContent.reviewCount,
        currentView = reviewCountView,
        defaultColor = UIColor.colorWithRed(
            red = 71.0 / 255.0,
            green = 85.0 / 255.0,
            blue = 105.0 / 255.0,
            alpha = 1.0,
        ),
        defaultTextSize = 11.0,
        defaultBold = false,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        textAlign = textAlign,
        lineHeight = lineHeight,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        assign = { view ->
            reviewCountView = view
            adView.setReviewCountView(view)
        },
    )
}

@Composable
private fun NativeAdScope.TextAsset(
    modifier: Modifier,
    text: String?,
    currentView: UILabel?,
    defaultColor: UIColor,
    defaultTextSize: Double,
    defaultBold: Boolean,
    color: Color,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
    softWrap: Boolean,
    maxLines: Int,
    minLines: Int,
    assign: (UILabel) -> Unit,
) {
    if (text.isNullOrEmpty()) return

    UIKitView(
        modifier = modifier,
        factory = {
            currentView ?: UILabel(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)).also { view ->
                assign(view)
                scheduleBind()
            }
        },
        update = { view ->
            view.text = text
            applyComposeTextStyle(
                view = view,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                textAlign = textAlign,
                lineHeight = lineHeight,
                softWrap = softWrap,
                maxLines = maxLines,
                minLines = minLines,
                defaultColor = defaultColor,
                defaultTextSize = defaultTextSize,
                defaultBold = defaultBold,
            )
            assign(view)
            scheduleBind()
        },
    )
}

private fun CASNativeView.applyKmpTemplateStyle(
    backgroundColor: Color?,
    primaryColor: Color?,
    primaryTextColor: Color?,
    headlineTextColor: Color?,
    headlineFontStyle: CASNativeTemplateFontStyle?,
    secondaryTextColor: Color?,
    secondaryFontStyle: CASNativeTemplateFontStyle?,
) {
    backgroundColor?.let {
        this.backgroundColor = it.toUIColor(UIColor.whiteColor)
    }

    primaryColor?.let { color ->
        val uiColor = color.toUIColor(defaultPrimaryColor())
        adLabelView()?.textColor = uiColor
        callToActionView()?.backgroundColor = uiColor
    }

    primaryTextColor?.let { color ->
        callToActionView()?.setTitleColor(
            color.toUIColor(UIColor.whiteColor),
            forState = UIControlStateNormal,
        )
        callToActionView()?.titleLabel?.textColor = color.toUIColor(UIColor.whiteColor)
    }

    headlineTextColor?.let { color ->
        headlineView()?.textColor = color.toUIColor(defaultHeadlineColor())
    }

    headlineFontStyle?.let { style ->
        updateFont(headlineView(), style)
    }

    secondaryTextColor?.let { color ->
        val uiColor = color.toUIColor(UIColor.darkGrayColor)

        bodyView()?.textColor = uiColor
        advertiserView()?.textColor = uiColor
        storeView()?.textColor = uiColor
        priceView()?.textColor = uiColor
        reviewCountView()?.textColor = uiColor
    }

    secondaryFontStyle?.let { style ->
        updateFont(bodyView(), style)
        updateFont(advertiserView(), style)
        updateFont(storeView(), style)
        updateFont(priceView(), style)
        updateFont(reviewCountView(), style)
    }
}

private fun updateFont(view: UILabel?, style: CASNativeTemplateFontStyle) {
    if (view == null) return
    val size = view.font.pointSize
    view.font = when (style) {
        CASNativeTemplateFontStyle.BOLD -> UIFont.boldSystemFontOfSize(size)
        CASNativeTemplateFontStyle.ITALIC -> UIFont.italicSystemFontOfSize(size)
        CASNativeTemplateFontStyle.MONOSPACE -> UIFont.monospacedSystemFontOfSize(
            size,
            weight = 0.0
        )
        CASNativeTemplateFontStyle.MEDIUM -> UIFont.systemFontOfSize(size, weight = 0.23)
        CASNativeTemplateFontStyle.NORMAL -> UIFont.systemFontOfSize(size)
    }
}

private fun applyComposeTextStyle(
    view: UILabel,
    color: Color,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
    softWrap: Boolean,
    maxLines: Int,
    minLines: Int,
    defaultColor: UIColor,
    defaultTextSize: Double,
    defaultBold: Boolean,
) {
    view.textColor = color.toUIColor(defaultColor)

    val size = if (fontSize == TextUnit.Unspecified) {
        defaultTextSize
    } else {
        fontSize.value.toDouble()
    }

    view.font = createFont(
        size = size,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        defaultBold = defaultBold,
    )

    view.textAlignment = when (textAlign) {
        TextAlign.Center -> NSTextAlignmentCenter
        TextAlign.End, TextAlign.Right -> NSTextAlignmentRight
        TextAlign.Justify -> NSTextAlignmentJustified
        TextAlign.Start, TextAlign.Left -> NSTextAlignmentLeft
        else -> NSTextAlignmentNatural
    }

    view.numberOfLines = when {
        !softWrap -> 1
        maxLines == Int.MAX_VALUE -> 0
        else -> maxLines.toLong()
    }

    view.lineBreakMode = if (softWrap) {
        NSLineBreakByTruncatingTail
    } else {
        NSLineBreakByClipping
    }
}

private fun createFont(
    size: Double,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    defaultBold: Boolean,
): UIFont {
    val isBold = fontWeight?.let { it.weight >= 600 } ?: defaultBold
    val isItalic = fontStyle == FontStyle.Italic

    return when {
        fontFamily == FontFamily.Monospace -> {
            UIFont.monospacedSystemFontOfSize(
                fontSize = size,
                weight = if (isBold) 0.4 else 0.0,
            )
        }

        isItalic -> UIFont.italicSystemFontOfSize(size)
        isBold -> UIFont.boldSystemFontOfSize(size)
        else -> UIFont.systemFontOfSize(size)
    }
}

private fun Color.toUIColor(defaultColor: UIColor): UIColor {
    if (this == Color.Unspecified) return defaultColor

    val argb = toArgb()
    val alpha = ((argb shr 24) and 0xFF) / 255.0
    val red = ((argb shr 16) and 0xFF) / 255.0
    val green = ((argb shr 8) and 0xFF) / 255.0
    val blue = (argb and 0xFF) / 255.0

    return UIColor.colorWithRed(
        red = red,
        green = green,
        blue = blue,
        alpha = alpha,
    )
}

private fun UILabel.applyAdLabelBackground(color: Color) {
    textAlignment = NSTextAlignmentCenter
    layer.cornerRadius = 999.0
    layer.borderWidth = 1.0
    layer.borderColor = color.toUIColor(defaultPrimaryColor()).CGColor
    layer.masksToBounds = true
}

private fun CASNativeScaleType?.toUIViewContentMode(): UIViewContentMode {
    return when (this) {
        CASNativeScaleType.CENTER -> UIViewContentMode.UIViewContentModeCenter
        CASNativeScaleType.CENTER_CROP -> UIViewContentMode.UIViewContentModeScaleAspectFill
        CASNativeScaleType.CENTER_INSIDE -> UIViewContentMode.UIViewContentModeScaleAspectFit
        CASNativeScaleType.FIT_CENTER -> UIViewContentMode.UIViewContentModeScaleAspectFit
        CASNativeScaleType.FIT_END -> UIViewContentMode.UIViewContentModeBottom
        CASNativeScaleType.FIT_START -> UIViewContentMode.UIViewContentModeTop
        CASNativeScaleType.FIT_XY -> UIViewContentMode.UIViewContentModeScaleToFill
        CASNativeScaleType.MATRIX -> UIViewContentMode.UIViewContentModeScaleAspectFit
        null -> UIViewContentMode.UIViewContentModeScaleAspectFit
    }
}

private fun defaultPrimaryColor(): UIColor {
    return UIColor.colorWithRed(
        red = 37.0 / 255.0,
        green = 99.0 / 255.0,
        blue = 235.0 / 255.0,
        alpha = 1.0,
    )
}

private fun defaultHeadlineColor(): UIColor {
    return UIColor.colorWithRed(
        red = 15.0 / 255.0,
        green = 23.0 / 255.0,
        blue = 42.0 / 255.0,
        alpha = 1.0,
    )
}