package com.cleveradssolutions.plugin.kmp.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.cleveradssolutions.plugin.kmp.CASNativeAdContent
import com.cleveradssolutions.plugin.kmp.CASNativeScaleType
import com.cleveradssolutions.plugin.kmp.CASNativeTemplateFontStyle

/**
 * Shows native ad content using a custom Compose layout.
 *
 * The composable creates a native ad root view internally.
 * Each asset composable inside [content] registers the corresponding native asset view.
 *
 * Advertising requirements:
 * - The ad view must be visible and non-transparent.
 * - Do not edit the text content of assets after binding.
 * - Do not edit the content of images.
 * - Native ads smaller than 32x32dp may not serve.
 * - At a single point in time, a loaded ad can only be served in one view.
 * - The app must be active, not running in the background.
 *
 * @param adContent Native ad content to display.
 * @param modifier Modifier applied to the native ad root.
 * @param content Native ad asset layout.
 */
@Composable
public expect fun CASNativeAd(
    adContent: CASNativeAdContent,
    modifier: Modifier = Modifier,
    content: @Composable NativeAdScope.() -> Unit,
): Unit

/**
 * Shows native ad content using predefined SDK templates.
 *
 * This composable integrates native ads into your app using predefined templates,
 * simplifying the process of ad display.
 *
 * The template size is taken from [modifier].
 * If the modifier has no visible size, the template may not be displayed correctly.
 *
 * Usage instructions:
 * - Do not register any custom asset views.
 * - Use this composable for template rendering.
 * - Use [CASNativeAd] when you need a custom layout.
 *
 * @param adContent Native ad content to display.
 * @param maxWidth The maximum width in [Dp].
 * @param maxHeight The maximum height in [Dp].
 * @param modifier Modifier that should define the template size.
 * @param backgroundColor Optional template background color.
 * @param primaryColor Optional primary color used for CTA, rating and ad label.
 * @param primaryTextColor Optional primary text color.
 * @param headlineTextColor Optional headline text color.
 * @param headlineFontStyle Optional headline font style.
 * @param secondaryTextColor Optional secondary text color.
 * @param secondaryFontStyle Optional secondary font style.
 */
@Composable
public expect fun CASNativeAdTemplate(
    adContent: CASNativeAdContent,
    maxWidth: Dp,
    maxHeight: Dp,
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    primaryColor: Color? = null,
    primaryTextColor: Color? = null,
    headlineTextColor: Color? = null,
    headlineFontStyle: CASNativeTemplateFontStyle? = null,
    secondaryTextColor: Color? = null,
    secondaryFontStyle: CASNativeTemplateFontStyle? = null,
): Unit

/**
 * Scope used for composing native ad asset views.
 */
public expect class NativeAdScope {
    /**
     * Native ad content displayed by this scope.
     */
    public val adContent: CASNativeAdContent
}

/**
 * Displays the AdChoices view.
 */
@Composable
public expect fun NativeAdScope.AdChoices(
    modifier: Modifier = Modifier,
): Unit

/**
 * Displays advertiser text.
 *
 * Text identifies the advertiser, for example advertiser name, brand name, or visible URL.
 * This text may truncate after 25 characters.
 */
@Composable
public expect fun NativeAdScope.AdAdvertiserText(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
): Unit

/**
 * Displays store text.
 *
 * Store is the name of the store where the product or service is available.
 */
@Composable
public expect fun NativeAdScope.AdStoreText(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
): Unit

/**
 * Displays ad label text, such as "Sponsored" or "Ad".
 *
 * Indicates that the content is an advertisement.
 */
@Composable
public expect fun NativeAdScope.AdLabelText(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
): Unit

/**
 * Displays body text.
 *
 * Body is the secondary body text, for example the article or app description.
 * This text may truncate after 90 characters.
 */
@Composable
public expect fun NativeAdScope.AdBodyText(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
): Unit

/**
 * Displays call-to-action button.
 *
 * Button or text field that encourages users to take action,
 * for example "Visit site", "Install", "Learn More", or "Buy Now".
 * This text may truncate after 15 characters.
 */
@Composable
public expect fun NativeAdScope.AdCallToAction(
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    backgroundColor: Color = Color.Black,
    shape: Shape = RectangleShape,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = FontWeight.Bold,
    fontFamily: FontFamily? = null,
    textAlign: TextAlign? = TextAlign.Center,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
): Unit

/**
 * Displays headline text.
 *
 * Headline is typically a short, attention-grabbing statement or title for the ad.
 */
@Composable
public expect fun NativeAdScope.AdHeadlineText(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
): Unit

/**
 * Displays the small app icon or advertiser logo with square aspect ratio 1:1.
 */
@Composable
public expect fun NativeAdScope.AdIconImage(
    modifier: Modifier = Modifier,
): Unit

/**
 * Displays the media content of the native ad.
 *
 * This is a larger visual element associated with the ad,
 * such as a promotional image, graphic, or video content.
 */
@Composable
public expect fun NativeAdScope.AdMedia(
    modifier: Modifier = Modifier,
    scaleType: CASNativeScaleType? = null,
): Unit

/**
 * Displays price text.
 *
 * Price provides information about the cost of the product or service being promoted.
 */
@Composable
public expect fun NativeAdScope.AdPriceText(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
): Unit

/**
 * Displays the rating from 0-5 that represents the average rating of the app in a store.
 */
@Composable
public expect fun NativeAdScope.AdStarRating(
    modifier: Modifier = Modifier,
): Unit

/**
 * Displays review count text.
 *
 * Review count provides context about the app's popularity and user feedback.
 */
@Composable
public expect fun NativeAdScope.AdReviewCountText(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
): Unit