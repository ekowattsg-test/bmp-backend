INSERT INTO param (param_key, value_string, changeable)
VALUES ('chatProjectGroupDefaultScope', 'LEADERSHIP', 1)
ON CONFLICT (param_key) DO UPDATE SET value_string = EXCLUDED.value_string, changeable = EXCLUDED.changeable;

INSERT INTO param (param_key, value_string, changeable)
VALUES ('chatAllowProjectGroupScopeChoice', '1', 1)
ON CONFLICT (param_key) DO UPDATE SET value_string = EXCLUDED.value_string, changeable = EXCLUDED.changeable;
