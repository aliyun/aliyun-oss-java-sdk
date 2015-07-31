/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyun.oss.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表示一条Conditions项。
 */
class ConditionItem {
	/**
	 * Conditions元组类型，目前支持二元组（{ ... }）、三元组（[ ... ]）。
	 */
	enum TupleType {
	    Two,
	    Three
	};
	
    private String name;
    private MatchMode matchMode;
    private String value;
    private TupleType tupleType;
    private long minimum;
    private long maximum;

    public ConditionItem(String name, String value) {
        this.matchMode = MatchMode.Exact;
        this.name = name;
        this.value = value;
        this.tupleType = TupleType.Two;
    }

    public ConditionItem(String name, long min, long max) {
        this.matchMode = MatchMode.Range;
        this.name = name;
        this.minimum = min;
        this.maximum = max;
        this.tupleType = TupleType.Three;
    }

    public ConditionItem(MatchMode matchMode, String name, String value) {
        this.matchMode = matchMode;
        this.name = name;
        this.value = value;
        this.tupleType = TupleType.Three;
    }

    public String jsonize() {
        String jsonizedCond = null;
        switch (tupleType) {
            case Two:
                jsonizedCond = String.format("{\"%s\":\"%s\"},", name, value);
                break;
            case Three:
                switch (matchMode) {
                    case Exact:
                        jsonizedCond = String.format("[\"eq\",\"$%s\",\"%s\"],", name, value);
                        break;
                    case StartWith:
                        jsonizedCond = String.format("[\"starts-with\",\"$%s\",\"%s\"],", name, value);
                        break;
                    case Range:
                        jsonizedCond = String.format("[\"content-length-range\",%d,%d],", minimum, maximum);
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Unsupported match mode %s", matchMode.toString()));
                }
                break;
        }

        return jsonizedCond;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MatchMode getMatchMode() {
		return matchMode;
	}

	public void setMatchMode(MatchMode matchMode) {
		this.matchMode = matchMode;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TupleType getTupleType() {
		return tupleType;
	}

	public void setTupleType(TupleType tupleType) {
		this.tupleType = tupleType;
	}

	public long getMinimum() {
		return minimum;
	}

	public void setMinimum(long minimum) {
		this.minimum = minimum;
	}

	public long getMaximum() {
		return maximum;
	}

	public void setMaximum(long maximum) {
		this.maximum = maximum;
	}
}

/**
 * Conditions列表，用于指定 Post 请求表单域的合法值。
 */
public class PolicyConditions {
    public final static String COND_CONTENT_LENGTH_RANGE = "content-length-range";
    public final static String COND_CACHE_CONTROL = "Cache-Control";
    public final static String COND_CONTENT_TYPE = "Content-Type";
    public final static String COND_CONTENT_DISPOSITION = "Content-Disposition";
    public final static String COND_CONTENT_ENCODING = "Content-Encoding";
    public final static String COND_EXPIRES = "Expires";
    public final static String COND_KEY = "key";
    public final static String COND_SUCCESS_ACTION_REDIRECT = "success_action_redirect";
    public final static String COND_SUCCESS_ACTION_STATUS = "success_action_status";
    public final static String COND_X_OSS_META_PREFIX = "x-oss-meta-";

    private static Map<String, List<MatchMode>> _supportedMatchRules = new HashMap<String, List<MatchMode>>(); 
    private List<ConditionItem> _conds = new ArrayList<ConditionItem>(); 

    static {
    	List<MatchMode> ordinaryMatchModes = new ArrayList<MatchMode>();
    	ordinaryMatchModes.add(MatchMode.Exact);
    	ordinaryMatchModes.add(MatchMode.StartWith);
    	List<MatchMode> specialMatchModes = new ArrayList<MatchMode>();
    	specialMatchModes.add(MatchMode.Range);

        _supportedMatchRules.put(COND_CONTENT_LENGTH_RANGE, specialMatchModes);

        _supportedMatchRules.put(COND_CACHE_CONTROL, ordinaryMatchModes);
        _supportedMatchRules.put(COND_CONTENT_TYPE, ordinaryMatchModes);
        _supportedMatchRules.put(COND_CONTENT_DISPOSITION, ordinaryMatchModes);
        _supportedMatchRules.put(COND_CONTENT_ENCODING, ordinaryMatchModes);
        _supportedMatchRules.put(COND_EXPIRES, ordinaryMatchModes);

        _supportedMatchRules.put(COND_KEY, ordinaryMatchModes);
        _supportedMatchRules.put(COND_SUCCESS_ACTION_REDIRECT, ordinaryMatchModes);
        _supportedMatchRules.put(COND_SUCCESS_ACTION_STATUS, ordinaryMatchModes);
        _supportedMatchRules.put(COND_X_OSS_META_PREFIX, ordinaryMatchModes);
    }

    /**
     * 采用默认匹配方式（精确匹配）添加Conditions项。
     * @param name Condition名称。
     * @param value Condition数值。
     */
    public void addConditionItem(String name, String value) {
        checkMatchModes(MatchMode.Exact, name);
        _conds.add(new ConditionItem(name, value));
    }

    /**
     * 采用指定匹配模式添加Conditions项。
     * @param matchMode Conditions匹配方式。
     * @param name Condition名称。
     * @param value Condition数值。
     */
    public void addConditionItem(MatchMode matchMode, String name, String value) {
        checkMatchModes(matchMode, name);
        _conds.add(new ConditionItem(matchMode, name, value));
    }

    /**
     * 采用范围匹配模式添加Conditions项。
     * @param name Condition名称。
     * @param min 范围最小值。
     * @param max 范围最小值。
     */
    public void addConditionItem(String name, long min, long max) {
        if (min > max)
            throw new IllegalArgumentException(String.format("Invalid range [%d, %d].", min, max));
        _conds.add(new ConditionItem(name, min, max));
    }

    private void checkMatchModes(MatchMode matchMode, String condName) {
        if (_supportedMatchRules.containsKey(condName)) {
            List<MatchMode> mms = _supportedMatchRules.get(condName);
            if (!mms.contains(matchMode))
                throw new IllegalArgumentException(String.format("Unsupported match mode for condition item %s", condName));
        }
    }

    public String jsonize() {
    	StringBuilder jsonizedConds = new StringBuilder();
        jsonizedConds.append("\"conditions\":[");
        for (ConditionItem cond : _conds)
            jsonizedConds.append(cond.jsonize());
        if (_conds.size() > 0)
            jsonizedConds.deleteCharAt(jsonizedConds.length() - 1);
        jsonizedConds.append("]");
        return jsonizedConds.toString();
    }
}
