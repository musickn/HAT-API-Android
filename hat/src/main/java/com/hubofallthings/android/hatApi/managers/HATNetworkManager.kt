/*
 * *
 *  * Copyright (C) 2018-2019 DataSwift Ltd
 *  *
 *  * SPDX-License-Identifier: MPL2
 *  *
 *  * This file is part of the Hub of All Things project (HAT).
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/
 *
 */

package com.hubofallthings.android.hatApi.managers

import android.net.UrlQuerySanitizer
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result

interface NetworkLayer {
    fun getRequest(url: String, parameters: List<Pair<String, Any?>>? = null, headers: Map<String, String>?, completion: (r: ResultType?) -> Unit)
    fun postRequest(url: String, body: String, headers: Map<String, String>, completion: (r: ResultType?) -> Unit)
    fun putRequest(url: String, body: String, headers: Map<String, String>, completion: (r: ResultType?) -> Unit)
    fun uploadRequest(url: String, body: String, headers: Map<String, String>, completion: (r: ResultType?) -> Unit)
    fun deleteRequest(url: String, parameters: List<Pair<String, Any?>>?, headers: Map<String, String>?, completion: (r: ResultType?) -> Unit)
    fun getRequestString(url: String, parameters: List<Pair<String, Any?>>? = null, headers: Map<String, String>, completion: (r: ResultType?) -> Unit)
}

enum class ResultType(var statusCode: Int?, var error: Error?, var json: com.github.kittinunf.fuel.android.core.Json?, var resultString: String?, var token: String?) {
    IsSuccess(null, null, null, null, null),
    HasFailed(null, null, null, null, null);
}

class HATNetworkManager : NetworkLayer {
    var resultType: ResultType? = null

    override fun getRequest(url: String, parameters: List<Pair<String, Any?>>?, headers: Map<String, String>?, completion: (r: ResultType?) -> Unit) {

        if (headers != null)
        FuelManager.instance.baseHeaders = headers
        Fuel.get(url, parameters).responseJson { _, response, result ->
            when (result) {

                is Result.Failure -> {

                    val ex = result.getException()
                    val error = Error(ex)

                    val test = ResultType.HasFailed
                    test.statusCode = response.statusCode
                    test.error = error
                    test.json = null
                    test.resultString = null
                    test.token = null

                    resultType = test
                    completion(resultType)
                }
                is Result.Success -> {

                    val token = response.headers["X-Auth-Token"]?.last()
                    val test2 = result.component1()

                    val test = ResultType.IsSuccess
                    test.statusCode = response.statusCode
                    test.error = null
                    test.json = test2
                    test.resultString = null
                    test.token = token

                    resultType = test
                    completion(resultType)
                }
            }
        }
    }

    override fun getRequestString(url: String, parameters: List<Pair<String, Any?>>?, headers: Map<String, String>, completion: (r: ResultType?) -> Unit) {

        url.httpGet(parameters).responseString { _, response, result ->
            when (result) {

                is Result.Failure -> {

                    val ex = result.getException()
                    val error = Error(ex)
                    val test = ResultType.HasFailed
                    test.statusCode = response.statusCode
                    test.error = error
                    test.json = null
                    test.resultString = null
                    test.token = null

                    resultType = test
                    completion(resultType)
                }
                is Result.Success -> {
                    val token = response.headers["X-Auth-Token"]?.last()
                    val test2 = result.component1()
                    val test = ResultType.IsSuccess
                    test.statusCode = response.statusCode
                    test.error = null
                    test.json = null
                    test.resultString = test2
                    test.token = token

                    resultType = test
                    completion(resultType)
                }
            }
        }
    }

    override fun postRequest(url: String, body: String, headers: Map<String, String>, completion: (r: ResultType?) -> Unit) {
        val timeout = 35000 // 35 seconds.
        val timeoutRead = 35000 // 35 seconds.
        FuelManager.instance.baseHeaders = headers

        Fuel.post(url).body(body).timeout(timeout).timeoutRead(timeoutRead).responseJson { _, response, result ->
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    val error = Error(ex)
                    val test = ResultType.HasFailed
                    test.statusCode = response.statusCode
                    test.error = error
                    test.json = null
                    test.resultString = null
                    test.token = null

                    resultType = test
                    completion(resultType)
                }
                is Result.Success -> {
                    val token = response.headers["X-Auth-Token"]?.last()
                    val test2 = result.component1()
                    val test = ResultType.IsSuccess
                    test.statusCode = response.statusCode
                    test.error = null
                    test.json = test2
                    test.resultString = null
                    test.token = token

                    resultType = test
                    completion(resultType)
                }
            }
        }
    }
    fun getQueryStringParameter(url: String?, param: String): String? {

        if (!url.isNullOrEmpty()) {

            val sanitizer = UrlQuerySanitizer()
            sanitizer.allowUnregisteredParamaters = true
            sanitizer.parseUrl(url)
            return sanitizer.getValue(param)
        }

        return null
    }

    override fun putRequest(url: String, body: String, headers: Map<String, String>, completion: (r: ResultType?) -> Unit) {
        FuelManager.instance.baseHeaders = headers

        Fuel.put(url).body(body).responseJson { _, response, result ->
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    val error = Error(ex)
                    val test = ResultType.HasFailed
                    test.statusCode = response.statusCode
                    test.error = error
                    test.json = null
                    test.resultString = null
                    test.token = null

                    resultType = test
                    completion(resultType)
                }
                is Result.Success -> {
                    val token = response.headers["X-Auth-Token"]?.last()
                    val test2 = result.component1()
                    val test = ResultType.IsSuccess
                    test.statusCode = response.statusCode
                    test.error = null
                    test.json = test2
                    test.resultString = null
                    test.token = token

                    resultType = test
                    completion(resultType)
                }
            }
        }
    }

    override fun uploadRequest(url: String, body: String, headers: Map<String, String>, completion: (r: ResultType?) -> Unit) {
        FuelManager.instance.baseHeaders = headers

        Fuel.upload(url).body(body).responseJson { _, response, result ->
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    val error = Error(ex)
                    val test = ResultType.HasFailed
                    test.statusCode = response.statusCode
                    test.error = error
                    test.json = null
                    test.resultString = null
                    test.token = null

                    resultType = test
                    completion(resultType)
                }
                is Result.Success -> {
                    val token = response.headers["X-Auth-Token"]?.last()
                    val test2 = result.component1()
                    val test = ResultType.IsSuccess
                    test.statusCode = response.statusCode
                    test.error = null
                    test.json = test2
                    test.resultString = null
                    test.token = token

                    resultType = test
                    completion(resultType)
                }
            }
        }
    }

    override fun deleteRequest(url: String, parameters: List<Pair<String, Any?>>?, headers: Map<String, String>?, completion: (r: ResultType?) -> Unit) {
        if (headers != null)
            FuelManager.instance.baseHeaders = headers
        Fuel.delete(url, parameters).responseJson { _, response, result ->
            // do something with response
            when (result) {

                is Result.Failure -> {

                    val ex = result.getException()
                    val error = Error(ex)

                    val test = ResultType.HasFailed
                    test.statusCode = response.statusCode
                    test.error = error
                    test.json = null
                    test.resultString = null
                    test.token = null

                    resultType = test
                    completion(resultType)
                }
                is Result.Success -> {

                    val token = response.headers["X-Auth-Token"]?.last()
                    val test2 = result.component1()

                    val test = ResultType.IsSuccess
                    test.statusCode = response.statusCode
                    test.error = null
                    test.json = test2
                    test.resultString = null
                    test.token = token

                    resultType = test
                    completion(resultType)
                }
            }
        }
    }
}
