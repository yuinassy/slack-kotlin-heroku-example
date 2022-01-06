import com.slack.api.model.kotlin_extension.view.blocks
import com.slack.api.model.view.View
import com.slack.api.model.view.Views

fun buildArigatoView(): View {
    return Views.view { thisView ->
        thisView
            .callbackId(Const.CallbackId.arigato)
            .type("modal")
            .notifyOnClose(true)
            .title(Views.viewTitle { it.type("plain_text").text("Arigato You").emoji(true) })
            .submit(Views.viewSubmit { it.type("plain_text").text("OK").emoji(true) })
            .close(Views.viewClose { it.type("plain_text").text("キャンセル").emoji(true) })
            .privateMetadata("""{"someData":"someValue"}""")
            .blocks {
                section {
                    markdownText("To")
                    usersSelect {
                        placeholder("佐藤")
                        actionId("to-select-action")
                    }

                }

                input {
                    blockId("point-block")
                    plainTextInput {
                        actionId("point-action")
                        multiline(true)
                    }
                    label("ポイント", emoji = true)
                }
                input {
                    blockId("agenda-block")
                    plainTextInput {
                        actionId("agenda-action")
                        multiline(true)
                    }
                    label("Detailed Agenda", emoji = true)
                }
                section {
                    blockId("category-block")
                    markdownText("Select a category of the meeting!")
                    staticSelect {
                        actionId(Const.Action.categorySelection)
                        placeholder("Select a category")
                        options {
                            option {
                                text("plain_text", "Customer")
                                value("customer")
                            }
                            option {
                                text("plain_text", "Partner")
                                value("partner")
                            }
                            option {
                                text("plain_text", "Internal")
                                value("internal")
                            }
                        }
                    }
                }
                input {
                    blockId("agenda-block")
                    plainTextInput {
                        actionId("agenda-action")
                        multiline(true)
                    }
                    label("Detailed Agenda", emoji = true)
                }
            }
    }
}
