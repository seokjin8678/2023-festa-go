package com.festago.support.spec

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec

abstract class UnitDescribeSpec(body: DescribeSpec.() -> Unit = {}) : DescribeSpec(body) {

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf
}
