package org.steellemm.mdlpprops

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


internal class UtilsKtTest {

    val template = """
"/mdlp/config/aggregation/executor/poolSize": "{{ mdlp_config_aggregation_executor_poolSize }}"
"/mdlp/config/aggregation/kafka/groupName": "{{ mdlp_config_aggregation_kafka_groupName }}"
"/mdlp/config/aggregation/maxProcessingDays": "{{ mdlp_config_aggregation_maxProcessingDays }}"
"/mdlp/config/api/account/manage/rest/port": "{{ mdlp_config_api_account_manage_rest_port }}"
"/mdlp/configs/tasks/workers/mdlp-task-worker::lk-postgres/tasks/topics/cancelled/group-id": "{{ mdlp_configs_tasks_workers_mdlp_task_worker_lk_postgres_tasks_topics_cancelled_group_id }}"
    """.trimIndent()

    @Test
    fun template() {
        getTreeFromTemplate(template.byteInputStream())
    }

    val values = """
mdlp_config_aggregation_executor_poolSize: '5'
mdlp_config_aggregation_kafka_groupName: 'NOnode'
mdlp_config_aggregation_maxProcessingDays: 'NOnode'
mdlp_config_api_account_manage_rest_port: 'NOnode'
mdlp_config_api_auth_rest_port: 'NOnode'
    """.trimIndent()

    @Test
    fun values() {
        val valueMap = getValueMap(values.byteInputStream())
        assertEquals(5, valueMap.size)
        assertTrue(valueMap.containsKey("mdlp_config_aggregation_executor_poolSize"))
        assertEquals("5", valueMap["mdlp_config_aggregation_executor_poolSize"])
    }

}