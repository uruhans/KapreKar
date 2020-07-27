package com.coloplast.olympus.ui.feature.guides.models

import com.coloplast.olympus.R

object GuideContent {

  // these listeners could possible be done in a nicer way - but for now this is how it's done
  fun getGuideContent(
    guideType: GuideType,
    topMenuListener: TopMenuItem.Listener,
    buttonListener: ButtonItem.Listener
  ): List<Any> {
    return when (guideType) {
      GuideType.UNKNOWN -> {
        throw NotImplementedError("UNKNOWN is not supported - did you make a mistake?")
      }
      GuideType.APPLICATION -> {
        listOf(
          TopMenuItem(topMenuListener),
          HeaderItem(R.string.guide_change_plate_title),
          ArticleItem(
            R.string.guide_change_plate_sub_title_1,
            R.drawable.guide_change_plate_1,
            R.string.guide_change_plate_body_1_text
          ),
          ArticleItem(
            image = R.drawable.guide_change_plate_2,
            description = R.string.guide_change_plate_body_2_text
          ),
          ArticleItem(
            image = R.drawable.guide_change_plate_3,
            description = R.string.guide_change_plate_body_3_text
          ),
          ArticleItem(
            image = R.drawable.guide_change_plate_4,
            description = R.string.guide_change_plate_body_4_text
          ),
          ArticleItem(
            R.string.guide_change_plate_sub_title_2,
            R.drawable.guide_change_plate_5,
            R.string.guide_change_plate_body_5_text
          ),
          ArticleItem(
            image = R.drawable.guide_change_plate_6,
            description = R.string.guide_change_plate_body_6_text
          ),
          ArticleItem(
            image = R.drawable.guide_change_plate_7,
            description = R.string.guide_change_plate_body_7_text
          ),
          ArticleItem(image = R.drawable.guide_change_plate_8),
          ButtonItem(R.string.generic_go_back, ButtonType.BACK, buttonListener)
        )
      }
      GuideType.APPLICATION_TIPS -> {
        listOf(
          TopMenuItem(topMenuListener),
          HeaderItem(R.string.guide_moisture_title, R.string.guide_moisture_body),
          ButtonItem(R.string.guide_moisture_link, ButtonType.ACTION, buttonListener),
          ButtonItem(R.string.generic_go_back, ButtonType.BACK, buttonListener)
        )
      }
      GuideType.CHARGING -> {
        listOf(
          TopMenuItem(topMenuListener),
          HeaderItem(R.string.guide_moisture_title, R.string.guide_moisture_body),
          ButtonItem(R.string.guide_moisture_link, ButtonType.ACTION, buttonListener),
          ButtonItem(R.string.generic_go_back, ButtonType.BACK, buttonListener)
        )
      }
      GuideType.CLEANING -> {
        listOf(
          TopMenuItem(topMenuListener),
          HeaderItem(R.string.guide_cleaning_title),
          ArticleItem(description = R.string.guide_cleaning_1_text),
          ArticleItem(
            R.string.guide_cleaning_sub_title_2,
            R.drawable.guide_cleaning_2,
            R.string.guide_cleaning_2_text
          ),
          ArticleItem(
            R.string.guide_cleaning_sub_title_3,
            R.drawable.guide_cleaning_3,
            R.string.guide_cleaning_3_text
          ),
          ArticleItem(
            title = R.string.guide_cleaning_sub_title_4,
            description = R.string.guide_cleaning_4_text
          ),
          ArticleItem(
            title = R.string.guide_cleaning_sub_title_5,
            description = R.string.guide_cleaning_5_text
          ),
          ArticleItem(
            title = R.string.guide_wiping_title,
            description = R.string.guide_wiping_1_text
          ),
          ArticleItem(
            title = R.string.guide_wiping_sub_title_2,
            description = R.string.guide_wiping_2_text
          ),
          ArticleItem(
            R.string.guide_wiping_sub_title_3,
            R.drawable.guide_wiping_1,
            R.string.guide_wiping_3_text
          ),
          ArticleItem(
            title = R.string.guide_wiping_sub_title_4,
            description = R.string.guide_wiping_4_text
          ),
          ArticleItem(
            title = R.string.guide_wiping_sub_title_5,
            description = R.string.guide_wiping_5_text
          ),
          ArticleItem(
            R.string.guide_wiping_sub_title_6,
            R.drawable.guide_wiping_2,
            R.string.guide_wiping_6_text
          ),
          ButtonItem(R.string.generic_go_back, ButtonType.BACK, buttonListener)
        )
      }
      GuideType.CONNECTION -> {
        listOf(
          TopMenuItem(topMenuListener),
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
          ButtonItem(R.string.guide_cg_button_text, ButtonType.LINK, buttonListener)
        )
      }
      GuideType.GET_STARTED -> {
        listOf(
          TopMenuItem(topMenuListener),
          HeaderItem(header = R.string.guide_get_started_title),
          ArticleItem(
            R.string.guide_get_started_sub_title_1,
            R.drawable.guide_get_started_1,
            R.string.guide_get_started_1_text
          ),
          ArticleItem(
            R.string.guide_get_started_sub_title_2,
            R.drawable.guide_get_started_2,
            R.string.guide_get_started_2_text
          ),
          ArticleItem(
            image = R.drawable.guide_get_started_3,
            description = R.string.guide_get_started_3_text
          ),
          ArticleItem(
            image = R.drawable.guide_get_started_4,
            description = R.string.guide_get_started_4_text
          ),
          ArticleItem(
            image = R.drawable.guide_get_started_5,
            description = R.string.guide_get_started_5_text
          ),
          ArticleItem(
            R.string.guide_get_started_sub_title_6,
            R.drawable.guide_get_started_6,
            R.string.guide_get_started_6_text
          ),
          ArticleItem(
            image = R.drawable.guide_get_started_7,
            description = R.string.guide_get_started_7_text
          ),
          ArticleItem(
            image = R.drawable.guide_get_started_8,
            description = R.string.guide_get_started_8_text
          ),
          ArticleItem(image = R.drawable.guide_get_started_9),
          ArticleItem(
            R.string.guide_get_started_sub_title_10,
            R.drawable.guide_get_started_10,
            R.string.guide_get_started_10_text
          ),
          ArticleItem(
            image = R.drawable.guide_get_started_11,
            description = R.string.guide_get_started_11_text
          ),
          ArticleItem(
            title = R.string.guide_get_started_sub_title_12,
            description = R.string.guide_get_started_12_text
          ),
          ButtonItem(R.string.generic_go_back, ButtonType.BACK, buttonListener)
        )
      }
      GuideType.LEARN_MORE -> {
        listOf(
          TopMenuItem(topMenuListener),
          HeaderItem(R.string.guide_learn_more_title, R.string.guide_learn_more_body),
          ButtonItem(R.string.generic_go_back, ButtonType.BACK, buttonListener)
        )
      }
    }
  }
}