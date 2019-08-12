package io.jenkins.plugins.issueinject.IssueInjectConfiguration

import lib.FormTagLib

/**
 * Created by Pierre Beitz
 * on 2019-08-07.
 */
namespace(FormTagLib).with {
    section(title: _('Issue Inject')) {
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