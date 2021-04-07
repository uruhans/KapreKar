package com.coloplast.olympus.ui.feature.guides.articles.models

import com.coloplast.olympus.R
import com.coloplast.olympus.ui.feature.guides.models.ButtonType

object GuideContent {

  fun getGuideContent(
    guideType: GuideType,
    buttonCallback: (ButtonType, Int) -> Unit
  ): List<Any> {
    return when (guideType) {
      GuideType.UNKNOWN -> {
        throw NotImplementedError("UNKNOWN is not supported - did you make a mistake?")
      }
      GuideType.DRY_APPLYING_TIPS -> {
        listOf(
          HeaderItem(
            header = R.string.guide_da_title,
            subHeader = R.string.guide_da_subtitle
          ),
          ArticleItem(
            image = R.drawable.guide_dry_applying_tips,
            title = R.string.guide_da_article_1_header,
            description = R.string.guide_da_article_1_body
          )
        )
      }
      GuideType.CHARGING -> {
        listOf(
          HeaderItem(R.string.guide_ch_title, R.string.guide_ch_subtitle),
          ArticleItem(
            image = R.drawable.guide_charging,
            title = R.string.guide_ch_article_1_header,
            description = R.string.guide_ch_article_1_body
          )
        )
      }
      GuideType.CLEANING -> {
        listOf(
          HeaderItem(
            header = R.string.guide_cl_title,
            subHeader = R.string.guide_cl_subtitle
          ),
          ArticleItem(description = R.string.guide_cl_article_1_body),
          ArticleItem(
            image = R.drawable.guide_cleaning_2,
            title = R.string.guide_cl_article_2_header,
            description = R.string.guide_cl_article_2_body
          ),
          ButtonItem(
            text = R.string.guide_cl_button_text,
            buttonType = ButtonType.LINK,
            clickCallback = buttonCallback
          ),
          ArticleItem(
            image = R.drawable.guide_cleaning_3,
            title = R.string.guide_cl_article_3_header,
            description = R.string.guide_cl_article_3_body
          )
        )
      }
      GuideType.UNDERSTANDING_SENSORS -> {
        listOf(
          HeaderItem(
            header = R.string.guide_us_title,
            subHeader = R.string.guide_us_subtitle_1
          ),
          ArticleItem(description = R.string.guide_us_body_1),
          ArticleItem(
            image = R.drawable.us1_there_might_be_a_problem,
            title = R.string.guide_us_subtitle_2,
            description = R.string.guide_us_body_2
          ),
          ButtonItem(
            text = R.string.guide_us_body_link_1,
            buttonType = ButtonType.LINK,
            clickCallback = buttonCallback
          ),
          ArticleItem(
            image = R.drawable.us2_sensors_worn_out,
            title = R.string.guide_us_subtitle_3,
            description = R.string.guide_us_body_3
          ),
          ButtonItem(
            text = R.string.guide_us_body_link_1,
            buttonType = ButtonType.LINK,
            clickCallback = buttonCallback
          ),
          ArticleItem(
            image = R.drawable.us3_sensor_or_skin_wet,
            title = R.string.guide_us_subtitle_4,
            description = R.string.guide_us_body_4
          ),
          ButtonItem(
            text = R.string.guide_us_body_link_2,
            buttonType = ButtonType.LINK,
            clickCallback = buttonCallback
          ),
          ArticleItem(
            image = R.drawable.us4_sensor_layer_damaged,
            title = R.string.guide_us_subtitle_5,
            description = R.string.guide_us_body_5
          ),
          ButtonItem(
            text = R.string.guide_us_body_link_1,
            buttonType = ButtonType.LINK,
            clickCallback = buttonCallback
          )
        )
      }
      GuideType.DRYING -> {
        listOf(
          HeaderItem(
            header = R.string.guide_dg_title,
            subHeader = R.string.guide_dg_subtitle
          ),
          ArticleItem(
            description = R.string.guide_dg_article_1_body
          ),
          ArticleItem(
            image = R.drawable.guide_article_image_1,
            title = R.string.guide_dg_article_2_header,
            description = R.string.guide_dg_article_2_body
          ),
          ArticleItem(
            image = R.drawable.guide_article_image_2,
            title = R.string.guide_dg_article_3_header,
            description = R.string.guide_dg_article_3_body
          )
        )
      }
      GuideType.CONNECTION -> {
        listOf(
          HeaderItem(R.string.guide_cg_title, R.string.guide_cg_subtitle),
          ArticleItem(
            image = R.drawable.guide_connection_1,
            title = R.string.guide_cg_subsection_1_header,
            description = R.string.guide_cg_subsection_1_body
          ),
          ArticleItem(
            title = R.string.guide_cg_subsection_2_header,
            description = R.string.guide_cg_subsection_2_body
          ),
          ButtonItem(R.string.guide_cg_button_text, ButtonType.LINK, buttonCallback)
        )
      }
      GuideType.LEARN_MORE -> {
        listOf(
          HeaderItem(R.string.guide_learn_more_title, R.string.guide_learn_more_body),
          ButtonItem(R.string.generic_go_back, ButtonType.BACK, buttonCallback)
        )
      }
      GuideType.CHECK_BATTERY -> listOf(
        HeaderItem(
          header = R.string.guide_cb_title,
          subHeader = R.string.guide_cb_subtitle
        ),
        ArticleItem(
          image = R.drawable.guide_check_battery_level,
          title = R.string.guide_cb_article_1_header,
          description = R.string.guide_cb_article_1_body
        ),
        ArticleItem(
          image = R.drawable.guide_view_attached_transmitter,
          title = R.string.guide_cb_article_2_header,
          description = R.string.guide_cb_article_2_body
        )
      )
      GuideType.DELETE_TRANSMITTER -> listOf(
        HeaderItem(
          header = R.string.guide_dt_title,
          subHeader = R.string.guide_dt_subtitle
        ),
        ArticleItem(
          description = R.string.guide_dt_article_1_body
        ),
        ArticleItem(
          image = R.drawable.guide_delete_transmitter,
          title = R.string.guide_dt_article_2_header,
          description = R.string.guide_dt_article_2_body
        ),
        ArticleItem(
          image = R.drawable.guide_delete_transmitter_second,
          title = R.string.guide_dt_article_3_header,
          description = R.string.guide_dt_article_3_body
        ),
        ButtonItem(
          R.string.guide_dt_button_text,
          ButtonType.ACTION,
          buttonCallback
        )
      )
    }
  }
}