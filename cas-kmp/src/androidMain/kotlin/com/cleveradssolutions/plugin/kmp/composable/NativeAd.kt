package com.cleveradssolutions.plugin.kmp.composable

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import com.cleveradssolutions.plugin.kmp.CASNativeAdContent
import com.cleveradssolutions.plugin.kmp.CASNativeScaleType
import com.cleveradssolutions.plugin.kmp.CASNativeTemplateFontStyle
import com.cleveradssolutions.sdk.nativead.CASChoicesView
import com.cleveradssolutions.sdk.nativead.CASMediaView
import com.cleveradssolutions.sdk.nativead.CASNativeView
import com.cleveradssolutions.sdk.nativead.CASStarRatingView
import com.cleversolutions.ads.AdSize
import android.graphics.Color as AndroidColor

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
): Unit {
    key(adContent, maxWidth, maxHeight) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                CASNativeView(context).apply {
                    setAdTemplateSize(
                        AdSize.getInlineBanner(maxWidth.value.toInt(), maxHeight.value.toInt()),
                    )
                    bindAdContent(adContent.android, true)
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
    val context = LocalContext.current

    val bindingScope = remember(adContent.android) {
        com.cleveradssolutions.plugin.kmp.composable.NativeAdScope(
            adView = CASNativeView(context).apply {
                id = View.generateViewId()
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                clipChildren = false
                clipToPadding = false
            },
            adContent = adContent,
        )
    }

    AndroidView(
        modifier = modifier,
        factory = {
            bindingScope.adView.apply {
                removeAllViews()

                addView(
                    ComposeView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        clipChildren = false
                        clipToPadding = false

                        setContent {
                            bindingScope.content()
                        }
                    },
                )
                bindingScope.scheduleBind()
            }
        },
        onRelease = { view ->
            // The view belongs to the composition, not to AndroidView, so nothing detaches it when
            // this composable goes away. Leaving it attached lets the SDK lay out an ad whose
            // content is already gone, which it reports as a render failure.
            (view.parent as? ViewGroup)?.removeView(view)
        },
    )
}

public actual class NativeAdScope internal constructor(
    internal val adView: CASNativeView,
    public actual val adContent: CASNativeAdContent,
) {
    internal var advertiserView: TextView? = null
    internal var bodyView: TextView? = null
    internal var priceView: TextView? = null
    internal var reviewCountView: TextView? = null
    internal var starRatingView: CASStarRatingView? = null
    internal var storeView: TextView? = null
    internal var adLabelView: TextView? = null
    internal var iconView: ImageView? = null
    internal var mediaView: CASMediaView? = null
    internal var adChoicesView: CASChoicesView? = null
    internal var callToActionView: Button? = null

    internal fun scheduleBind(): Unit {
        adView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                adView.viewTreeObserver.removeOnPreDrawListener(this)
                adView.bindAdContent(adContent.android)
                return true
            }
        })
    }
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
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                adView.headlineView = this
            }
        },
        update = { view ->
            view.text = adContent.headline.orEmpty()

            applyComposeTextStyle(
                view = view,
                context = view.context,
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
                defaultColor = AndroidColor.rgb(15, 23, 42),
                defaultTextSize = 18f,
                defaultTypeface = Typeface.DEFAULT_BOLD,
            )

            adView.headlineView = view
        },
    )
}

@Composable
public actual fun NativeAdScope.AdChoices(
    modifier: Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            adChoicesView ?: CASChoicesView(context).also { view ->
                adChoicesView = view
                adView.adChoicesView = view
            }
        },
        update = { view ->
            adChoicesView = view
            adView.adChoicesView = view
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
        defaultColor = AndroidColor.rgb(37, 99, 235),
        defaultTextSize = 12f,
        defaultTypeface = Typeface.DEFAULT_BOLD,
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
            adView.advertiserView = view
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

    AndroidView(
        modifier = modifier,
        factory = { context ->
            adLabelView ?: TextView(context).apply {
                adLabelView = this
                adView.adLabelView = this
            }
        },
        update = { view ->
            view.text = text

            applyComposeTextStyle(
                view = view,
                context = view.context,
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
                defaultColor = AndroidColor.rgb(37, 99, 235),
                defaultTextSize = 12f,
                defaultTypeface = Typeface.DEFAULT,
            )

            view.applyAdLabelBackground(view.context, color)

            adLabelView = view
            adView.adLabelView = view
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
        defaultColor = AndroidColor.rgb(51, 65, 85),
        defaultTextSize = 13f,
        defaultTypeface = Typeface.DEFAULT,
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
            adView.bodyView = view
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
    AndroidView(
        modifier = modifier,
        factory = { context ->
            callToActionView ?: Button(context).apply {
                minHeight = 0
                minimumHeight = 0
                minWidth = 0
                minimumWidth = 0
                includeFontPadding = false
                setPadding(0, 0, 0, 0)
                isAllCaps = false
                gravity = android.view.Gravity.CENTER

                callToActionView = this
                adView.callToActionView = this
            }
        },
        update = { view ->
            view.text = adContent.callToAction ?: "Details"

            view.minHeight = 0
            view.minimumHeight = 0
            view.minWidth = 0
            view.minimumWidth = 0
            view.includeFontPadding = false
            view.setPadding(0, 0, 0, 0)
            view.isAllCaps = false

            applyComposeTextStyle(
                view = view,
                context = view.context,
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
                defaultColor = AndroidColor.WHITE,
                defaultTextSize = 14f,
                defaultTypeface = Typeface.DEFAULT_BOLD,
            )

            view.gravity = android.view.Gravity.CENTER
            view.setPadding(0, 0, 0, 0)

            view.background = GradientDrawable().apply {
                setColor(backgroundColor.toArgb())
                cornerRadius = view.context.dp(12).toFloat()
            }

            callToActionView = view
            adView.callToActionView = view
        },
    )
}

@Composable
public actual fun NativeAdScope.AdIconImage(
    modifier: Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            iconView ?: ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER

                iconView = this
                adView.iconView = this
            }
        },
        update = { view ->
            view.scaleType = ImageView.ScaleType.FIT_CENTER

            iconView = view
            adView.iconView = view
        },
    )
}

@Composable
public actual fun NativeAdScope.AdMedia(
    modifier: Modifier,
    scaleType: CASNativeScaleType?,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            mediaView ?: CASMediaView(context).apply {
                setBackgroundColor(AndroidColor.rgb(241, 245, 249))

                mediaView = this
                adView.mediaView = this
            }
        },
        update = { view ->
            mediaView = view
            adView.mediaView = view
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
        defaultColor = AndroidColor.rgb(15, 23, 42),
        defaultTextSize = 14f,
        defaultTypeface = Typeface.DEFAULT_BOLD,
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
            adView.priceView = view
        },
    )
}

@Composable
public actual fun NativeAdScope.AdStarRating(
    modifier: Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            starRatingView ?: CASStarRatingView(context).apply {
                color = AndroidColor.rgb(37, 99, 235)

                starRatingView = this
                adView.starRatingView = this
            }
        },
        update = { view ->
            starRatingView = view
            adView.starRatingView = view
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
        defaultColor = AndroidColor.rgb(71, 85, 105),
        defaultTextSize = 11f,
        defaultTypeface = Typeface.DEFAULT,
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
            adView.reviewCountView = view
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
        defaultColor = AndroidColor.rgb(15, 23, 42),
        defaultTextSize = 14f,
        defaultTypeface = Typeface.DEFAULT,
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
            adView.storeView = view
        },
    )
}

@Composable
private fun NativeAdScope.TextAsset(
    modifier: Modifier,
    text: String?,
    currentView: TextView?,
    defaultColor: Int,
    defaultTextSize: Float,
    defaultTypeface: Typeface,
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
    assign: (TextView) -> Unit,
) {
    if (text.isNullOrEmpty()) return

    AndroidView(
        modifier = modifier,
        factory = { context ->
            currentView ?: TextView(context).apply {
                assign(this)
            }
        },
        update = { view ->
            view.text = text

            applyComposeTextStyle(
                view = view,
                context = view.context,
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
                defaultTypeface = defaultTypeface,
            )

            assign(view)
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
        setBackgroundColor(it.toArgb())
    }

    val primary = primaryColor?.toArgb()
    if (primary != null) {
        adLabelView?.setTextColor(primary)

        adLabelView?.background = GradientDrawable().apply {
            setColor(AndroidColor.TRANSPARENT)
            cornerRadius = context.dp(2).toFloat()
            setStroke(context.dp(1), primary)
        }

        (starRatingView as? CASStarRatingView)?.color = primary

        callToActionView?.background = GradientDrawable().apply {
            setColor(primary)
            cornerRadius = context.dp(20).toFloat()
        }
    }

    primaryTextColor?.let { color ->
        callToActionView?.setTextColor(color.toArgb())
    }

    headlineTextColor?.let { color ->
        headlineView?.setTextColor(color.toArgb())
    }

    headlineFontStyle?.let { style ->
        headlineView?.typeface = style.toAndroidTypeface()
    }

    secondaryTextColor?.let { color ->
        val textColor = color.toArgb()

        bodyView?.setTextColor(textColor)
        advertiserView?.setTextColor(textColor)
        storeView?.setTextColor(textColor)
        priceView?.setTextColor(textColor)
        reviewCountView?.setTextColor(textColor)
    }

    secondaryFontStyle?.let { style ->
        val typeface = style.toAndroidTypeface()

        bodyView?.typeface = typeface
        advertiserView?.typeface = typeface
        storeView?.typeface = typeface
        priceView?.typeface = typeface
        reviewCountView?.typeface = typeface
    }
}

private fun CASNativeTemplateFontStyle.toAndroidTypeface(): Typeface {
    return when (this) {
        CASNativeTemplateFontStyle.BOLD -> Typeface.DEFAULT_BOLD
        CASNativeTemplateFontStyle.ITALIC -> Typeface.defaultFromStyle(Typeface.ITALIC)
        CASNativeTemplateFontStyle.MONOSPACE -> Typeface.MONOSPACE
        CASNativeTemplateFontStyle.MEDIUM -> Typeface.create("sans-serif-medium", Typeface.NORMAL)
        CASNativeTemplateFontStyle.NORMAL -> Typeface.DEFAULT
    }
}

private fun android.content.Context.pxToDp(value: Int): Int {
    return (value / resources.displayMetrics.density).toInt()
}

private fun android.content.Context.dp(value: Int): Int {
    return (value * resources.displayMetrics.density).toInt()
}

private fun applyComposeTextStyle(
    view: TextView,
    context: Context,
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
    defaultColor: Int,
    defaultTextSize: Float,
    defaultTypeface: Typeface,
) {
    if (color == Color.Unspecified) {
        view.setTextColor(defaultColor)
    } else {
        view.setTextColor(color.toArgb())
    }

    if (fontSize == TextUnit.Unspecified) {
        view.textSize = defaultTextSize
    } else {
        view.textSize = fontSize.value
    }

    val baseTypeface = when (fontFamily) {
        FontFamily.SansSerif -> Typeface.SANS_SERIF
        FontFamily.Serif -> Typeface.SERIF
        FontFamily.Monospace -> Typeface.MONOSPACE
        FontFamily.Cursive -> Typeface.create("cursive", Typeface.NORMAL)
        else -> defaultTypeface
    }

    val defaultIsBold = defaultTypeface == Typeface.DEFAULT_BOLD
    val isBold = fontWeight?.let { it.weight >= 600 } ?: defaultIsBold
    val isItalic = fontStyle == FontStyle.Italic

    val typefaceStyle = when {
        isBold && isItalic -> Typeface.BOLD_ITALIC
        isBold -> Typeface.BOLD
        isItalic -> Typeface.ITALIC
        else -> Typeface.NORMAL
    }

    view.typeface = Typeface.create(baseTypeface, typefaceStyle)

    view.gravity = when (textAlign) {
        TextAlign.Center -> android.view.Gravity.CENTER
        TextAlign.End, TextAlign.Right -> {
            android.view.Gravity.END or android.view.Gravity.CENTER_VERTICAL
        }

        TextAlign.Justify -> android.view.Gravity.FILL_HORIZONTAL
        else -> {
            android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL
        }
    }

    if (lineHeight != TextUnit.Unspecified &&
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P
    ) {
        view.lineHeight = android.util.TypedValue.applyDimension(
            android.util.TypedValue.COMPLEX_UNIT_SP,
            lineHeight.value,
            context.resources.displayMetrics,
        ).toInt()
    }

    if (!softWrap) {
        view.maxLines = 1
        view.setHorizontallyScrolling(true)
    } else {
        view.maxLines = maxLines
        view.minLines = minLines
        view.setHorizontallyScrolling(false)
    }

    view.includeFontPadding = false
}

private fun TextView.applyAdLabelBackground(
    context: Context,
    color: Color,
) {
    val borderColor = if (color == Color.Unspecified) {
        AndroidColor.rgb(37, 99, 235)
    } else {
        color.toArgb()
    }

    setPadding(
        context.dp(8),
        context.dp(3),
        context.dp(8),
        context.dp(3),
    )

    background = GradientDrawable().apply {
        setColor(AndroidColor.TRANSPARENT)
        cornerRadius = context.dp(999).toFloat()
        setStroke(context.dp(1), borderColor)
    }
}