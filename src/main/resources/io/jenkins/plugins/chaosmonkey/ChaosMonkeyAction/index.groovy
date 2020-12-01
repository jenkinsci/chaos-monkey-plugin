package io.jenkins.plugins.chaosmonkey.ChaosMonkeyAction

import lib.FormTagLib
import lib.LayoutTagLib

/**
 * Created by Pierre Beitz
 * on 20/09/2019.
 */

def l = namespace(LayoutTagLib)
def events = it.getEvents()

namespace(FormTagLib).with {
    l.layout(norefresh: 'true', permission: app.ADMINISTER) {
        l.main_panel {
            section(title: 'Lock The Queue') {
                form(name: 'chaos-monkey-trigger-deadlock', method: 'POST', action: 'lockTheQueue', type: 'submit') {
                    entry {
                        textbox(name: 'duration')
                    }
                    entry {
                        submit(value: 'Lock the Queue!')
                    }
                }
            }
            section(title: 'Generate Load') {
                form(name: 'chaos-monkey-trigger-load', method: 'POST', action: 'generateLoad', type: 'submit') {
                    entry {
                        textbox(name: 'duration')
                    }
                    entry {
                        submit(value: 'Generate Load on the Instance')
                    }
                }
            }
            section(title: 'Memory Leak Management') {
                form(name: 'chaos-monkey-trigger-memory-leak', method: 'POST', action: 'generateMemoryLeak', type: 'submit') {
                    entry {
                        submit(value: 'Start Memory Leak')
                    }
                }
                form(name: 'chaos-monkey-stop-memory-leak', method: 'POST', action: 'stopMemoryLeak', type: 'submit') {
                    entry {
                        submit(value: 'Stop Memory Leak')
                    }
                }
            }
            section(title: 'Lock events since the last restart') {
                block("""
                        <table class="bigtable pane">
                            <thead>
                                <tr>
                                    <th>Start time</th>
                                    <th>Duration</th>
                                    <th>Status</th>
                                    <th>Type</th>
                                </tr>
                            </thead>
                            ${events.collect { printEventEntry(it) }.join()}
                        </table>
                  """)
            }
        }
    }
}

def printEventEntry(e) {
    """
        <tr>
            <td>${e.startTime}</td>
            <td>${e.duration}</td>
            <td>${e.done ? 'DONE' : 'RUNNING'}</td>
            <td>${e.type}</td>
        </tr>
    """
}
