package org.formentor.magnolia.ai.infrastructure.azure;

import java.util.function.Supplier;

// TODO Share interface and implementation with ai-training
/**
 * Provider for Azure Api Key.
 */
public interface AzureApiKeyProvider extends Supplier<String> {

}
