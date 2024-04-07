package com.axllblc.worlddays.data.source;

import com.axllblc.worlddays.data.Result;

public interface WikipediaIntroSource {
    Result<String> getArticleIntro(String articleURL);
}
