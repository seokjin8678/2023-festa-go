package com.festago.logging.infrastructure.pattern

import com.festago.logging.domain.RequestLoggingPolicy
import com.festago.logging.domain.RequestLoggingUriPatternMatcher
import org.springframework.stereotype.Component

@Component
internal class TrieRequestLoggingUriPatternMatcher : RequestLoggingUriPatternMatcher {

    private val methodToNode: MutableMap<String, Node> = HashMap()

    override fun addPattern(method: String, path: String, policy: RequestLoggingPolicy) {
        var node = methodToNode.computeIfAbsent(method) { Node() }
        for (segment in toSegments(path)) {
            node = node.addNode(segment)
        }
        node.loggingPolicy = policy
    }

    private fun toSegments(uri: String): List<String> {
        if (uri.startsWith("/")) {
            return uri.drop(1).split("/")
        }
        return emptyList()
    }

    override fun match(method: String, path: String): RequestLoggingPolicy? {
        var node = methodToNode[method] ?: return null
        for (segment in toSegments(path)) {
            node = node.getNextNode(segment)
                ?: if (node.hasWildCardNode) node.wildCardNode!! else return null
        }
        return node.loggingPolicy
    }

    private inner class Node {
        private val children: MutableMap<String, Node> = HashMap()
        var loggingPolicy: RequestLoggingPolicy? = null

        fun addNode(segment: String): Node {
            return if (segment.startsWith("{")) {
                children.computeIfAbsent("*") { Node() }
            } else {
                children.computeIfAbsent(segment) { Node() }
            }
        }

        fun getNextNode(segment: String) = children[segment]

        val hasWildCardNode: Boolean
            get() = children.containsKey("*")

        val wildCardNode: Node?
            get() = children["*"]
    }
}
