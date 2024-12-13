package com.aliyun.oss.endpoint;

/**
 * resolve the endpoint like dns.
 *
 */

public interface EndpointResolver {
    /**
     * return the general api endpoint, the result can be ip or ip:port. if you don't do
     * anything like DefaultEndpointResolver. the http library will implement the dns resolve.
     * 
     * @param generalApiEndpoint
     * @return the endpoint addr
     */
    public String resolveGeneralApiEndpoint(String generalApiEndpoint);

    /**
     * return the get service api endpoint, the result can be ip or ip:port. if you don't do
     * anything like DefaultEndpointResolver. the http library will implement the dns resolve.
     * 
     * @param getServiceApiEndpoint
     * @return the endpoint
     */
    public String resolveGetServiceApiEndpoint(String getServiceApiEndpoint);
}
