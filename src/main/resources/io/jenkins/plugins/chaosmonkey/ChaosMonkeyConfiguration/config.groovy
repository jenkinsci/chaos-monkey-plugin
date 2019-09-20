package io.jenkins.plugins.chaosmonkey.ChaosMonkeyConfiguration

import lib.FormTagLib

/**
 * Created by Pierre Beitz
 * on 2019-08-07.
 */
namespace(FormTagLib).with {
    section(title: _('Chaos Monkey')) {
        entry(title: _('Latency Injection')) {
            repeatableProperty(field: 'latencies', add: 'Add latency item') {
                entry {
                    div(align: 'right') {
                        repeatableDeleteButton()
                    }
                }
            }
        }
        entry(title: _('DeadLock Injection')) {
            form {
                entry {
                    property(field: 'deadlockInjector')
                }
            }
        }
    }
}