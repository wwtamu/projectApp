'use strict';

describe('projectApp.version module', function() {
	beforeEach(module('projectApp.version'));

	describe('version service', function() {
		it('should return current version', inject(function(version) {
			expect(version).toEqual('0.1');
		}));
	});
});

