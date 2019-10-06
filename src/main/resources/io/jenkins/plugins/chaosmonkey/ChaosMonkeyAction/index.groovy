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
    l.layout(norefresh: 'true') {
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
            section(title: 'Lock events since the last restart') {
                block("""
                        <table class="bigtable pane">
                            <thead>
                                <tr>
                                    <th>Start time</th>
                                    <th>Duration</th>
                                    <th>Status</th>
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
        </tr>
    """
}
