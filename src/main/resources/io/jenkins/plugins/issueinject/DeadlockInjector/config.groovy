package io.jenkins.plugins.issueinject.DeadlockInjector

import lib.FormTagLib

/**
 * Created by Pierre Beitz
 * on 2019-08-07.
 */
def f = namespace(FormTagLib)

namespace(FormTagLib).with {
    entry(title: _('Duration'), field: 'duration') {
        f.textbox()
    }

    validateButton(
            title: _('Lock the Queue'),
            progress: _('Queue Locked...'),
            method: 'lockTheQueue',
            with: 'duration'
    )
}
