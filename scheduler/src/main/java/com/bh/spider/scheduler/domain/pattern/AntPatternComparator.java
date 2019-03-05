package com.bh.spider.scheduler.domain.pattern;

import com.bh.spider.common.fetch.Request;
import com.bh.spider.scheduler.domain.RulePattern;

import java.util.Comparator;

public   class AntPatternComparator implements Comparator<RulePattern> {
    private final Request request;

    public AntPatternComparator(Request request) {
        this.request= request;
    }

    private int compare0(String original, String pattern1, String pattern2) {
        PatternInfo info1 = new PatternInfo(pattern1);
        PatternInfo info2 = new PatternInfo(pattern2);
        //判断两个是否/**
        if (info1.isLeastSpecific() && info2.isLeastSpecific()) {
            return 0;
        } else if (info1.isLeastSpecific()) {
            return 1;
        } else if (info2.isLeastSpecific()) {
            return -1;
        } else {
            //判断两个pattern和request中path的是否相等(此处已修改，原为equals(this.path)
            boolean pattern1EqualsPath = pattern1.equals(original);
            boolean pattern2EqualsPath = pattern2.equals(original);
            if (pattern1EqualsPath && pattern2EqualsPath) {
                return 0;
            } else if (pattern1EqualsPath) {
                return -1;
            } else if (pattern2EqualsPath) {
                return 1;
            }
            //是否/abc/**类型
            else if (info1.isPrefixPattern() && info2.getDoubleWildcards() == 0) {
                return 1;
            } else if (info2.isPrefixPattern() && info1.getDoubleWildcards() == 0) {
                return -1;
            } else if (info1.getTotalCount() != info2.getTotalCount()) {
                return info1.getTotalCount() - info2.getTotalCount();
            } else if (info1.getLength() != info2.getLength()) {
                return info2.getLength() - info1.getLength();
            } else if (info1.getSingleWildcards() < info2.getSingleWildcards()) {
                return -1;
            } else if (info2.getSingleWildcards() < info1.getSingleWildcards()) {
                return 1;
            } else if (info1.getUriVars() < info2.getUriVars()) {
                return -1;
            } else {
                return info2.getUriVars() < info1.getUriVars() ? 1 : 0;
            }
        }
    }

    @Override
    public int compare(RulePattern o1, RulePattern o2) {
        int returnValue = compare0(request.url().getHost(),o1.domain(), o2.domain());
        if (returnValue == 0)
            returnValue = compare0(request.url().getPath(),o1.path(), o2.path());

        return returnValue;
    }

    private static class PatternInfo {
        private final String pattern;
        private int uriVars;
        private int singleWildcards;
        private int doubleWildcards;
        private boolean catchAllPattern;
        private boolean prefixPattern;
        private Integer length;

        public PatternInfo(String pattern) {
            this.pattern = pattern;
            if (this.pattern != null) {
                this.initCounters();
                this.catchAllPattern = this.pattern.equals("/**");
                this.prefixPattern = !this.catchAllPattern && this.pattern.endsWith("/**");
            }

            if (this.uriVars == 0) {
                this.length = this.pattern != null ? this.pattern.length() : 0;
            }

        }

        protected void initCounters() {
            int pos = 0;
            if (this.pattern != null) {
                while(true) {
                    while(pos < this.pattern.length()) {
                        if (this.pattern.charAt(pos) == '{') {
                            ++this.uriVars;
                            ++pos;
                        } else if (this.pattern.charAt(pos) == '*') {
                            if (pos + 1 < this.pattern.length() && this.pattern.charAt(pos + 1) == '*') {
                                ++this.doubleWildcards;
                                pos += 2;
                            } else if (pos > 0 && !this.pattern.substring(pos - 1).equals(".*")) {
                                ++this.singleWildcards;
                                ++pos;
                            } else {
                                ++pos;
                            }
                        } else {
                            ++pos;
                        }
                    }

                    return;
                }
            }
        }

        public int getUriVars() {
            return this.uriVars;
        }

        public int getSingleWildcards() {
            return this.singleWildcards;
        }

        public int getDoubleWildcards() {
            return this.doubleWildcards;
        }

        public boolean isLeastSpecific() {
            return this.pattern == null || this.catchAllPattern;
        }

        public boolean isPrefixPattern() {
            return this.prefixPattern;
        }

        public int getTotalCount() {
            return this.uriVars + this.singleWildcards + 2 * this.doubleWildcards;
        }

        public int getLength() {
            if (this.length == null) {
                this.length = this.pattern != null ? AntPatternMatcher.VARIABLE_PATTERN.matcher(this.pattern).replaceAll("#").length() : 0;
            }

            return this.length;
        }
    }
}
