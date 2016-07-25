/*
 * Copyright 2015 Ayuget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ayuget.redface.data.api.hfr.transforms;

import com.ayuget.redface.data.api.model.Topic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.functions.Func1;
import timber.log.Timber;

public class HTMLToTopic implements Func1<String, Topic> {
    private static final Pattern TOPIC_DETAILS_PATTERN = Pattern.compile("(?:<input type=\"hidden\" name=\"post\")(?:\\s*)(?:value=\")(\\d+)(?:\")(?:.*?)(?:<h3>)(.*?)(?:</h3>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Pattern TOPIC_PAGES_COUNT_PATTERN = Pattern.compile("(?:<a)(?:[^>]+)(?:class=\"cHeader\">)(\\d+)(?:</a></div>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @Override
    public Topic call(String s) {
        Matcher m = TOPIC_DETAILS_PATTERN.matcher(s);

        if (m.find()) {
            int topicId = Integer.valueOf(m.group(1));
            String topicSubject = m.group(2);

            Matcher pageCountMatcher = TOPIC_PAGES_COUNT_PATTERN.matcher(s);
            int pagesCount = pageCountMatcher.find() ? Integer.valueOf(pageCountMatcher.group(1)) : 1;

            // Sets up a topic with all vital informations for it to be displayed
            Topic topic = Topic.builder()
                    .isPrivateMessage(false)
                    .id(topicId)
                    .title(topicSubject)
                    .pagesCount(pagesCount)
                    .build();

            return topic;
        }
        else {
            Timber.e("Topic details not found");
        }

        return null;
    }
}
