package com.saudappstudio.snotificationmanager.provider

import com.saudappstudio.snotificationmanager.domain.model.CrashDetailModel
import com.saudappstudio.snotificationmanager.domain.model.CrashIssueModel
import com.saudappstudio.snotificationmanager.domain.model.CrashIssueStatus

/**
 * Provider supplying realistic sample crash datasets and diagnostic stack traces for demonstration and testing.
 */
object CrashlyticsProvider {

    val sampleIssues: List<CrashIssueModel> = listOf(
        CrashIssueModel(
            id = "crash_dict_01",
            appId = "app_dictionary",
            appName = "Advanced English Dictionary",
            packageName = "com.saudappstudio.dictionary",
            title = "java.lang.NullPointerException",
            subtitle = "Attempt to invoke virtual method 'int java.lang.String.length()' on a null object reference",
            topStackFrame = "DictionaryDatabase.kt:142 in searchWord()",
            crashCount = 142,
            userCount = 89,
            isFatal = true,
            status = CrashIssueStatus.OPEN,
            firstSeenTimestamp = System.currentTimeMillis() - 864000000L,
            lastSeenTimestamp = System.currentTimeMillis() - 1800000L,
            appVersion = "v2.4.1 (104)",
            androidVersion = "Android 14 (API 34)",
            deviceModel = "Samsung Galaxy S23 Ultra"
        ),
        CrashIssueModel(
            id = "crash_vocab_01",
            appId = "app_vocabulary",
            appName = "Vocabulary Builder",
            packageName = "com.saudappstudio.vocabulary",
            title = "java.lang.IndexOutOfBoundsException",
            subtitle = "Index: 10, Size: 10 in QuizAdapter.onBindViewHolder",
            topStackFrame = "QuizSessionManager.kt:89 in loadNextQuestion()",
            crashCount = 38,
            userCount = 24,
            isFatal = true,
            status = CrashIssueStatus.OPEN,
            firstSeenTimestamp = System.currentTimeMillis() - 432000000L,
            lastSeenTimestamp = System.currentTimeMillis() - 7200000L,
            appVersion = "v1.8.0 (88)",
            androidVersion = "Android 13 (API 33)",
            deviceModel = "Google Pixel 7a"
        ),
        CrashIssueModel(
            id = "crash_calc_01",
            appId = "app_calculator",
            appName = "Calculator",
            packageName = "com.saudappstudio.calculator",
            title = "java.lang.ArithmeticException",
            subtitle = "Divide by zero in high-precision trigonometry solver",
            topStackFrame = "MathEngine.kt:214 in executeDivision()",
            crashCount = 19,
            userCount = 15,
            isFatal = false,
            status = CrashIssueStatus.RESOLVED,
            firstSeenTimestamp = System.currentTimeMillis() - 1296000000L,
            lastSeenTimestamp = System.currentTimeMillis() - 86400000L,
            appVersion = "v3.1.2 (112)",
            androidVersion = "Android 12 (API 31)",
            deviceModel = "Xiaomi Redmi Note 12"
        ),
        CrashIssueModel(
            id = "crash_dict_02",
            appId = "app_dictionary",
            appName = "Advanced English Dictionary",
            packageName = "com.saudappstudio.dictionary",
            title = "java.lang.OutOfMemoryError",
            subtitle = "Failed to allocate a 32MB bitmap allocation for high-res phonetics diagram",
            topStackFrame = "PhoneticImageViewer.kt:67 in renderDiagram()",
            crashCount = 65,
            userCount = 41,
            isFatal = true,
            status = CrashIssueStatus.OPEN,
            firstSeenTimestamp = System.currentTimeMillis() - 604800000L,
            lastSeenTimestamp = System.currentTimeMillis() - 14400000L,
            appVersion = "v2.4.1 (104)",
            androidVersion = "Android 11 (API 30)",
            deviceModel = "OnePlus 9 Pro"
        ),
        CrashIssueModel(
            id = "crash_vocab_02",
            appId = "app_vocabulary",
            appName = "Vocabulary Builder",
            packageName = "com.saudappstudio.vocabulary",
            title = "retrofit2.HttpException",
            subtitle = "HTTP 504 Gateway Timeout fetching updated flashcard deck definitions",
            topStackFrame = "SyncRepositoryImpl.kt:105 in fetchRemoteDeck()",
            crashCount = 210,
            userCount = 130,
            isFatal = false,
            status = CrashIssueStatus.MUTED,
            firstSeenTimestamp = System.currentTimeMillis() - 1728000000L,
            lastSeenTimestamp = System.currentTimeMillis() - 3600000L,
            appVersion = "v1.8.0 (88)",
            androidVersion = "Android 14 (API 34)",
            deviceModel = "Motorola Edge 40"
        )
    )

    val sampleDetails: Map<String, CrashDetailModel> = mapOf(
        "crash_dict_01" to CrashDetailModel(
            id = "detail_dict_01",
            issueId = "crash_dict_01",
            stackTrace = """
                java.lang.NullPointerException: Attempt to invoke virtual method 'int java.lang.String.length()' on a null object reference
                	at com.saudappstudio.dictionary.data.db.DictionaryDatabase.searchWord(DictionaryDatabase.kt:142)
                	at com.saudappstudio.dictionary.domain.usecase.SearchWordUseCase.execute(SearchWordUseCase.kt:34)
                	at com.saudappstudio.dictionary.presentation.search.SearchViewModel${'$'}performSearch${'$'}1.invokeSuspend(SearchViewModel.kt:89)
                	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
                	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
                	at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:570)
                	at kotlinx.coroutines.scheduling.CoroutineScheduler${'$'}Worker.executeTask(CoroutineScheduler.kt:750)
                	at kotlinx.coroutines.scheduling.CoroutineScheduler${'$'}Worker.runWorker(CoroutineScheduler.kt:678)
                	at kotlinx.coroutines.scheduling.CoroutineScheduler${'$'}Worker.run(CoroutineScheduler.kt:665)
            """.trimIndent(),
            threadName = "main-ui-thread",
            osVersion = "Android 14 (API 34, UP1A.231005.007)",
            deviceModel = "Samsung Galaxy S23 Ultra (SM-S918B)",
            ramFreeMb = 2150L,
            diskFreeMb = 48200L,
            customKeys = mapOf(
                "query_term" to "serendipitous",
                "offline_mode" to "true",
                "database_version" to "14"
            )
        ),
        "crash_vocab_01" to CrashDetailModel(
            id = "detail_vocab_01",
            issueId = "crash_vocab_01",
            stackTrace = """
                java.lang.IndexOutOfBoundsException: Index: 10, Size: 10
                	at java.util.ArrayList.get(ArrayList.java:437)
                	at com.saudappstudio.vocabulary.presentation.quiz.QuizSessionManager.loadNextQuestion(QuizSessionManager.kt:89)
                	at com.saudappstudio.vocabulary.presentation.quiz.QuizViewModel.onAnswerSelected(QuizViewModel.kt:112)
                	at com.saudappstudio.vocabulary.presentation.quiz.QuizScreenKt${'$'}QuizContent${'$'}2${'$'}1.invoke(QuizScreen.kt:204)
                	at androidx.compose.foundation.clickableKt${'$'}clickable${'$'}4.invoke(Clickable.kt:156)
                	at androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNodeImpl${'$'}PointerEventHandlerCoroutine.invokeSuspend(SuspendingPointerInputFilter.kt:622)
            """.trimIndent(),
            threadName = "main",
            osVersion = "Android 13 (API 33, T1AM.220805.002)",
            deviceModel = "Google Pixel 7a",
            ramFreeMb = 1420L,
            diskFreeMb = 18900L,
            customKeys = mapOf(
                "current_question_index" to "10",
                "total_questions" to "10",
                "session_type" to "gre_verbal"
            )
        )
    )
}
